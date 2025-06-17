module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  extends: [
    'plugin:vue/vue3-recommended',
    'eslint:recommended'
  ],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  rules: {
    'vue/no-v-model-argument': 'off',  // 允许v-model带参数，用于Element Plus组件
    'vue/multi-word-component-names': 'off'  // 允许单个单词的组件名
  }
} 