<template>
  <div>
    <Navbar />
    <div class="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-lg">
      <h2 class="text-2xl font-semibold text-gray-900 mb-6">Update Product</h2>
      <form @submit.prevent="updateProduct" class="space-y-5">
        <input v-model="product.name" placeholder="Product Name" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model="product.description" placeholder="Description" class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model.number="product.costPrice" type="number" step="0.01" placeholder="Cost Price" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model.number="product.sellingPrice" type="number" step="0.01" placeholder="Selling Price" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model="product.supplier" placeholder="Supplier" class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model.number="product.initialQuantity" type="number" placeholder="Stock Quantity" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <button type="submit" class="w-full bg-indigo-600 text-white p-3 rounded-lg shadow hover:bg-indigo-700 transition font-medium">Update Product</button>
      </form>
      <p class="mt-4 text-center text-sm" :class="message.includes('successfully') ? 'text-green-600' : 'text-red-600'">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Navbar from '../components/Navbar.vue';

export default {
  components: {
    Navbar
  },
  data() {
    return {
      product: {
        name: '',
        description: '',
        costPrice: 0,
        sellingPrice: 0,
        supplier: '',
        initialQuantity: 0
      },
      message: ''
    };
  },
  async created() {
    await this.fetchProduct();
  },
  methods: {
    async fetchProduct() {
      try {
        const productId = this.$route.params.id;
        const response = await axios.get(`http://localhost:8080/api/products/${productId}`, {
          headers: { 'Authorization': 'Basic ' + btoa('admin:password123') }
        });
        // Fetch current stock from /api/inventory endpoint
        const inventoryResponse = await axios.get('http://localhost:8080/api/inventory', {
          headers: { 'Authorization': 'Basic ' + btoa('admin:password123') }
        });
        const productStock = inventoryResponse.data.find(p => p.productId === parseInt(productId))?.stock || 0;

        this.product = {
          name: response.data.name,
          description: response.data.description || '',
          costPrice: response.data.costPrice,
          sellingPrice: response.data.sellingPrice,
          supplier: response.data.supplier || '',
          initialQuantity: productStock
        };
      } catch (error) {
        this.message = 'Failed to load product: ' + (error.response?.data?.message || error.message);
        console.error('Fetch product error:', error.response?.data || error);
      }
    },
    async updateProduct() {
      try {
        const productId = this.$route.params.id;
        await axios.put(`http://localhost:8080/api/products/${productId}`, this.product, {
          headers: { 'Authorization': 'Basic ' + btoa('admin:password123') }
        });
        this.message = 'Product updated successfully!';
        setTimeout(() => this.$router.push('/dashboard'), 1000);
      } catch (error) {
        this.message = 'Failed to update product: ' + (error.response?.data?.message || error.message);
        console.error('Update product error:', error.response?.data || error);
      }
    }
  }
};
</script>