<template>
  <div>
    <Navbar />
    <div class="container mx-auto mt-8 px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <h2 class="text-3xl font-bold text-gray-900">Inventory Dashboard</h2>
        <p class="mt-1 text-sm text-gray-600">Manage hospital inventory efficiently</p>
      </div>

      <!-- Search and Filters -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 gap-4">
        <div class="flex-1">
          <input
              v-model="searchQuery"
              type="text"
              placeholder="Search products..."
              class="w-full sm:w-64 p-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              @input="filterProducts"
          />
        </div>
        <div class="flex items-center gap-4">
          <select
              v-model="stockFilter"
              class="p-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              @change="filterProducts"
          >
            <option value="all">All Stock</option>
            <option value="low">Low Stock (< 10)</option>
            <option value="out">Out of Stock</option>
          </select>
          <button
              @click="$router.push('/product-entry')"
              class="bg-indigo-600 text-white px-4 py-2 rounded-lg shadow hover:bg-indigo-700 transition"
          >
            Add Product
          </button>
          <button
              @click="$router.push('/product-exit')"
              class="bg-red-600 text-white px-4 py-2 rounded-lg shadow hover:bg-red-700 transition"
          >
            Log Exit
          </button>
        </div>
      </div>

      <!-- Inventory Table -->
      <div class="bg-white shadow-md rounded-lg overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Stock</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="product in filteredProducts" :key="product.productId" class="hover:bg-gray-50 transition">
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ product.name }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              <span :class="product.stock < 10 ? 'text-red-500' : 'text-green-500'">{{ product.stock }}</span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm space-x-4">
              <button
                  @click="$router.push(`/product-update/${product.productId}`)"
                  class="text-indigo-600 hover:text-indigo-800 font-medium"
              >
                Update
              </button>
              <button
                  @click="confirmDelete(product.productId, product.name)"
                  class="text-red-600 hover:text-red-800 font-medium"
              >
                Delete
              </button>
            </td>
          </tr>
          <tr v-if="filteredProducts.length === 0">
            <td colspan="3" class="px-6 py-4 text-center text-sm text-gray-500">No products found</td>
          </tr>
          </tbody>
        </table>
      </div>
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
      products: [],
      filteredProducts: [],
      searchQuery: '',
      stockFilter: 'all'
    };
  },
  async created() {
    await this.fetchInventory();
  },
  methods: {
    async fetchInventory() {
      try {
        const response = await axios.get('http://localhost:8080/api/inventory', {
          headers: { 'Authorization': 'Basic ' + btoa('admin:password123') }
        });
        this.products = response.data;
        this.filterProducts();
      } catch (error) {
        console.error('Failed to fetch inventory:', error);
      }
    },
    filterProducts() {
      let filtered = [...this.products];
      if (this.searchQuery) {
        filtered = filtered.filter(product =>
            product.name.toLowerCase().includes(this.searchQuery.toLowerCase())
        );
      }
      if (this.stockFilter === 'low') {
        filtered = filtered.filter(product => product.stock > 0 && product.stock < 10);
      } else if (this.stockFilter === 'out') {
        filtered = filtered.filter(product => product.stock === 0);
      }
      this.filteredProducts = filtered;
    },
    async confirmDelete(productId, productName) {
      const confirmed = window.confirm(`Are you sure you want to delete "${productName}"? This action cannot be undone.`);
      if (confirmed) {
        try {
          await axios.delete(`http://localhost:8080/api/products/${productId}`, {
            headers: { 'Authorization': 'Basic ' + btoa('admin:password123') }
          });
          await this.fetchInventory();
        } catch (error) {
          console.error('Failed to delete product:', error);
          alert('Failed to delete product: ' + (error.response?.data?.message || error.message));
        }
      }
    }
  }
};
</script>