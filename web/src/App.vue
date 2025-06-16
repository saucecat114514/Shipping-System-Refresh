<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header>
    <div class="wrapper">
      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/about">About</RouterLink>
        <RouterLink to="/login">Login</RouterLink>
        <RouterLink to="/Port/PortManagement">Port</RouterLink>
        <RouterLink to="/Ship/ShipManagement">Ship</RouterLink>
        <RouterLink to="/Order/OrderManagement">Order</RouterLink>
        <RouterLink to="/Voyage/VoyageManagement">Voyage</RouterLink>
        <RouterLink to="/Route/RouteManagement">Route</RouterLink>
      </nav>
    </div>
  </header>

  <RouterView />
</template>

<style scoped>
nav a {
  display: inline-block;
  padding: 0 1rem;
}
</style>
