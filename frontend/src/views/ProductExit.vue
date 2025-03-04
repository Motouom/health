<template>
  <div>
    <Navbar />
    <div class="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-lg">
      <h2 class="text-2xl font-semibold text-gray-900 mb-6">Log Product Exit</h2>
      <form @submit.prevent="logExit" class="space-y-5">
        <select v-model="transaction.productId" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm">
          <option value="">Select Product</option>
          <option v-for="product in products" :key="product.productId" :value="product.productId">
            {{ product.name }} (Stock: {{ product.stock }})
          </option>
        </select>
        <input v-model.number="transaction.quantity" type="number" placeholder="Quantity" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <select v-model="transaction.transactionType" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm">
          <option value="sale">Sale</option>
          <option value="use">Use</option>
          <option value="disposal">Disposal</option>
        </select>
        <input v-model="transaction.reason" placeholder="Reason" class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <button type="submit" class="w-full bg-indigo-600 text-white p-3 rounded-lg shadow hover:bg-indigo-700 transition font-medium">Log Exit</button>
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
      transaction: { productId: '', quantity: 0, transactionType: 'sale', reason: '' },
      products: [],
      message: ''
    };
  },
  async created() {
    await this.fetchProducts();
  },
  methods: {
    async fetchProducts() {
      try {
        const response = await axios.get('http://localhost:8080/api/inventory', {
          headers: { 'Authorization': 'Basic ' + btoa('admin:password123') }
        });
        this.products = response.data;
      } catch (error) {
        this.message = 'Failed to fetch products: ' + (error.response?.data || error.message);
      }
    },
    async logExit() {
      try {
        await axios.post('http://localhost:8080/api/transactions', this.transaction, {
          headers: { 'Authorization': 'Basic ' + btoa('admin:password123') }
        });
        this.message = 'Exit logged successfully!';
        this.transaction = { productId: '', quantity: 0, transactionType: 'sale', reason: '' };
        await this.fetchProducts();
      } catch (error) {
        this.message = error.response?.data?.message || 'Failed to log exit: ' + error.message;
      }
    }
  }
};
</script>