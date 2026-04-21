import { defineStore } from 'pinia'

export const useStore = defineStore('main', {
  state: () => ({
    globalhost: 'http://localhost:8086/'
  }),
  getters: {},
  actions: {}
})