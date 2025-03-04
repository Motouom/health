<template>
  <div>
    <Navbar />
    <div class="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-lg">
      <h2 class="text-2xl font-semibold text-gray-900 mb-6">Admin Login</h2>
      <form @submit.prevent="handleLogin" class="space-y-5">
        <input v-model="username" type="text" placeholder="Username" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <input v-model="password" type="password" placeholder="Password" required class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm" />
        <button type="submit" class="w-full bg-indigo-600 text-white p-3 rounded-lg shadow hover:bg-indigo-700 transition font-medium">Login</button>
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
      username: '',
      password: '',
      message: ''
    };
  },
  methods: {
    async handleLogin() {
      try {
        const response = await axios.post('http://localhost:8080/api/login', {
          username: this.username,
          password: this.password
        });
        this.message = response.data;
        if (response.data.includes("成功")) {
          this.$router.push('/dashboard');
        }
      } catch (error) {
        this.message = 'Login failed!';
      }
    }
  }
};
</script>