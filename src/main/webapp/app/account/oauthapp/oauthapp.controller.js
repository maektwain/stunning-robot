(function(){
    'use strict';

    angular
        .module('frontendApp')
        .controller('OauthAppController', OauthAppController);

    OauthAppController.$inject = ['$inject', '$state', 'OauthApps', 'AlertService' ];

    function OauthAppController ($scope, $state, OauthAppController,AlertService) {

        var vm = this;

        vm.loadAll = loadAll;

        loadAll();

        function loadAll () {

            OauthApp.get({

            }, onSuccess, onError);

           function onSuccess(data, headers) {
                vm.oauthapps = data;
           }

           function onError(error) {
            AlertService.error(error.data.message);
           }
        }

    }


})
