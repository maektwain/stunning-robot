(function (){

    'use strict';

    angular
        .module('frontendApp')
        .factory('ProductSearch', ProductSearch );

    ProductSearch.$inject = ['$resource'];

    function ProductSearch ($resource){

        var service = $resource('api/_search/products', {query:'@q'},{

            'query': {
                    method:'GET',
                    isArray: true
            }



        });

        return service;

    }


}) ();
