(function() {
    'use strict';

    angular
        .module('frontendApp')
        .controller('ProductsDialogController', ProductsDialogController);

    ProductsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Products'];

    function ProductsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Products) {
        var vm = this;

        vm.products = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.products.id !== null) {
                Products.update(vm.products, onSaveSuccess, onSaveError);
            } else {
                Products.save(vm.products, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('frontendApp:productsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
