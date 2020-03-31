var userApi = Vue.resource('/users');

Vue.component('user-row', {
  props: ['user'],
  template : '<div>{{user.username}} {{user.realname}}</div>'
});

Vue.component('users-list', {
  props: ['users'],
  template:
      '<div>' +
        '<user-row v-for="user in users" :key="user.username" :user="user" />' +
      '</div>',

  created: function() {
    userApi.get()
        //.then(result => result.text())
        //.then(test => console.log(test))
         .then(result => result.json().then(data =>
        // console.log(data)))
        data.forEach(user => this.users.push(user))))
  }
});

var app = new Vue({
  el: '#app',
  template: '<users-list :users="users" />',
  data: {
    users: []
  }
});