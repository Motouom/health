<template>
  <div>
    <Navbar />
    <div class="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-lg">
      <h2 class="text-2xl font-semibold text-gray-900 mb-6">Add New Product</h2>
      <form @submit.prevent="addProduct" class="space-y-5">
        <input v-model="product.name" placeholder="Product Name" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model="product.description" placeholder="Description" class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model.number="product.costPrice" type="number" step="0.01" placeholder="Cost Price" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model.number="product.sellingPrice" type="number" step="0.01" placeholder="Selling Price" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model="product.supplier" placeholder="Supplier" class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model.number="product.initialQuantity" type="number" placeholder="Initial Quantity" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <button type="submit" class="w-full bg-indigo-600 text-white p-3 rounded-lg shadow hover:bg-indigo-700 transition font-medium">Add Product</button>
      </form>
      <p class="mt-4 text-center text-sm text-gray-600">{{ message }}</p>
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
  methods: {
    async addProduct() {
      try {
        console.log('Sending product:', JSON.stringify(this.product, null, 2));
        const response = await axios.post('http://localhost:8080/api/products', this.product, {
          headers: { 'Authorization': 'Basic ' + btoa('admin:password123') }
        });
        const isExisting = this.product.name === response.data.name;
        this.message = isExisting
            ? `Added ${this.product.initialQuantity} units to existing product "${this.product.name}"`
            : 'New product added successfully!';
        this.product = { name: '', description: '', costPrice: 0, sellingPrice: 0, supplier: '', initialQuantity: 0 };
      } catch (error) {
        const errorMessage = error.response?.data?.message || error.response?.data?.error || error.message;
        console.error('Add product error:', error.response?.data || error);
        this.message = 'Failed to add product: ' + errorMessage;
      }
    }
  }
};
</script>