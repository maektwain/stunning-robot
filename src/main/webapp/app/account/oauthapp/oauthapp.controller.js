(function(){
    'use strict';

    angular
        .module('frontendApp')
        .controller('OauthAppController', OauthAppController);

    OauthAppController.$inject = ['$scope', '$state', 'OauthApps', 'AlertService' ];

    function OauthAppController ($scope, $state, OauthApps,AlertService) {

        var vm = this;

        vm.loadAll = loadAll;

        loadAll();

        function loadAll () {

            OauthApps.get({

            }, onSuccess, onError);

           function onSuccess(data, headers) {
                vm.oauthapps = data;
           }

           function onError(error) {
            AlertService.error(error.data.message);
           }
        }

    }


})();
