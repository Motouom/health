<template>
  <div>
    <Navbar />
    <div class="container mx-auto mt-8 px-4 sm:px-6 lg:px-8">
      <div class="mb-8">
        <h2 class="text-3xl font-bold text-gray-900">Sales Summary</h2>
        <p class="mt-1 text-sm text-gray-600">View detailed sales performance over time</p>
      </div>

      <div class="bg-white shadow-md rounded-lg p-6">
        <div class="flex flex-wrap gap-4 mb-6">
          <button
              @click="fetchSales('')"
              class="bg-indigo-500 text-white px-4 py-2 rounded-lg shadow hover:bg-indigo-600 transition"
          >
            All Time
          </button>
          <button
              @click="fetchSales('daily')"
              class="bg-indigo-500 text-white px-4 py-2 rounded-lg shadow hover:bg-indigo-600 transition"
          >
            Daily
          </button>
          <button
              @click="fetchSales('weekly')"
              class="bg-indigo-500 text-white px-4 py-2 rounded-lg shadow hover:bg-indigo-600 transition"
          >
            Weekly
          </button>
          <button
              @click="fetchSales('monthly')"
              class="bg-indigo-500 text-white px-4 py-2 rounded-lg shadow hover:bg-indigo-600 transition"
          >
            Monthly
          </button>
        </div>

        <div v-if="salesDetails" class="space-y-4">
          <p class="text-gray-700 text-sm">
            {{ salesDetails.period }} Sales:
            <span class="font-medium">{{ salesDetails.totalUnitsSold }}</span> units sold in
            <span class="font-medium">{{ salesDetails.numberOfSales }}</span> transactions
          </p>
          <div v-if="salesDetails.transactions && salesDetails.transactions.length > 0" class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Quantity Sold</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
              </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
              <tr v-for="(transaction, index) in salesDetails.transactions" :key="index" class="hover:bg-gray-50 transition">
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ transaction.productName }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ transaction.quantity }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(transaction.date) }}</td>
              </tr>
              </tbody>
            </table>
          </div>
          <div v-else class="text-gray-500 text-sm">
            No transactions recorded for this period.
          </div>
        </div>
        <div v-else class="text-gray-500 text-sm">
          No sales data available.
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Navbar from '../components/Navbar.vue';

export default {
  name: 'SalesSummary',
  components: {
    Navbar
  },
  data() {
    return {
      salesDetails: null,
      currentPeriod: ''
    };
  },
  async created() {
    await this.fetchSales(''); // Load all sales by default
  },
  methods: {
    async fetchSales(period) {
      try {
        const url = period ? `http://localhost:8080/api/sales?period=${period}` : 'http://localhost:8080/api/sales';
        const response = await axios.get(url, {
          headers: { 'Authorization': 'Basic ' + btoa('admin:password123') }
        });
        console.log('Sales Data:', JSON.stringify(response.data, null, 2)); // Pretty-print response
        this.salesDetails = response.data;
        this.currentPeriod = period;
      } catch (error) {
        console.error('Failed to fetch sales details:', error);
        this.salesDetails = null;
      }
    },
    formatDate(date) {
      return new Date(date).toLocaleString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  }
};
</script>