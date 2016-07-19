(function() {
    'use strict';

    angular
        .module('frontendApp')
        .controller('ProductsDetailController', ProductsDetailController);

    ProductsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Products'];

    function ProductsDetailController($scope, $rootScope, $stateParams, entity, Products) {
        var vm = this;

        vm.products = entity;

        var unsubscribe = $rootScope.$on('frontendApp:productsUpdate', function(event, result) {
            vm.products = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
