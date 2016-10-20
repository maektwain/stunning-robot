(function() {
    'use strict';

    angular
        .module('frontendApp')
        .controller('OauthAppDialogController', OauthAppDialogController);

     OauthAppDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity','OauthApps'];


     function OauthAppDialogController($timeout,$scope,$stateParams,$uibModalInstance,entity,OauthApps) {
        var vm = this;

        vm.oauthapp = entity;

        vm.clear = clear;

        vm.save = save;

        $timeout(function() {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;

                OauthApp.save(vm.oauthapp, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('frontendApp:oauthAppUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }
     }

})();
