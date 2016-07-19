'use strict';

describe('Controller Tests', function() {

    describe('CatalogType Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockCatalogType;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockCatalogType = jasmine.createSpy('MockCatalogType');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'CatalogType': MockCatalogType
            };
            createController = function() {
                $injector.get('$controller')("CatalogTypeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'frontendApp:catalogTypeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
