(function() {
    'use strict';

    angular
        .module('frontendApp')
        .controller('ProductsDeleteController',ProductsDeleteController);

    ProductsDeleteController.$inject = ['$uibModalInstance', 'entity', 'Products'];

    function ProductsDeleteController($uibModalInstance, entity, Products) {
        var vm = this;

        vm.products = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Products.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
