<template>
  <nav class="bg-indigo-600 shadow-lg">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <!-- Left: Brand -->
        <div class="flex-shrink-0 flex items-center">
          <router-link to="/" class="text-white text-xl font-bold">Hospital Inventory</router-link>
        </div>

        <!-- Right: Navigation Links -->
        <div class="flex items-center space-x-4">
          <router-link
              to="/dashboard"
              class="text-white hover:bg-indigo-700 px-3 py-2 rounded-md text-sm font-medium transition"
              active-class="bg-indigo-800"
          >
            Dashboard
          </router-link>
          <router-link
              to="/product-entry"
              class="text-white hover:bg-indigo-700 px-3 py-2 rounded-md text-sm font-medium transition"
              active-class="bg-indigo-800"
          >
            Add Product
          </router-link>
          <router-link
              to="/product-exit"
              class="text-white hover:bg-indigo-700 px-3 py-2 rounded-md text-sm font-medium transition"
              active-class="bg-indigo-800"
          >
            Log Exit
          </router-link>
          <router-link
              to="/sales-summary"
              class="text-white hover:bg-indigo-700 px-3 py-2 rounded-md text-sm font-medium transition"
              active-class="bg-indigo-800"
          >
            Sales Summary
          </router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'Navbar'
};
</script>