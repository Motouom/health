import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/views/Login.vue';
import Dashboard from '@/views/Dashboard.vue'; // Placeholder for now
import ProductEntry from "@/views/ProductEntry.vue";
import ProductExit from "@/views/ProductExit.vue";
import SalesSummary from '@/views/SalesSummary.vue';
import ProductUpdate from '../views/ProductUpdate.vue';


const routes = [
  { path: '/', component: Login },
  { path: '/dashboard', component: Dashboard },
  { path: '/product-entry', component: ProductEntry },
  { path: '/product-exit', component: ProductExit },
  { path: '/sales-summary', component: SalesSummary },
  { path: '/product-update/:id', component: ProductUpdate }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;