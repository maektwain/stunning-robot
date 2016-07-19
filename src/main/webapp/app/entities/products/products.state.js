(function() {
    'use strict';

    angular
        .module('frontendApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('products', {
            parent: 'entity',
            url: '/products?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Products'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/products/products.html',
                    controller: 'ProductsController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('products-detail', {
            parent: 'entity',
            url: '/products/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Products'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/products/products-detail.html',
                    controller: 'ProductsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Products', function($stateParams, Products) {
                    return Products.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('products.new', {
            parent: 'products',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/products/products-dialog.html',
                    controller: 'ProductsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                price: null,
                                brand: null,
                                model: null,
                                imageUrl: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('products', null, { reload: true });
                }, function() {
                    $state.go('products');
                });
            }]
        })
        .state('products.edit', {
            parent: 'products',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/products/products-dialog.html',
                    controller: 'ProductsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Products', function(Products) {
                            return Products.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('products', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('products.delete', {
            parent: 'products',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/products/products-delete-dialog.html',
                    controller: 'ProductsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Products', function(Products) {
                            return Products.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('products', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
