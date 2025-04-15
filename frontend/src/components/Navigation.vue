<template>
  <nav>
    <ul>
      <li><router-link to="/">Home</router-link></li>
      <li><router-link to="/chat">Chat</router-link></li>
      <li><router-link to="/documents">Documents</router-link></li>
      <li><router-link to="/qa">QA</router-link></li>
      <li><router-link to="/model-performance">Model Performance</router-link></li>
      <li><router-link to="/rag">RAG Chat</router-link></li>
    </ul>
  </nav>
</template>

<script>
export default {
  name: 'Navigation'
}
</script>

<style scoped>
nav {
  background-color: #f8f9fa;
  padding: 1rem;
}

ul {
  list-style: none;
  display: flex;
  gap: 1rem;
  margin: 0;
  padding: 0;
}

li {
  margin: 0;
}

a {
  text-decoration: none;
  color: #333;
  font-weight: 500;
}

a:hover {
  color: #007bff;
}

.router-link-active {
  color: #007bff;
}
</style> 