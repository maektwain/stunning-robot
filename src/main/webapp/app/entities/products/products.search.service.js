(function() {
    'use strict';

    angular
        .module('frontendApp')
        .factory('ProductsSearch', ProductsSearch);

    ProductsSearch.$inject = ['$resource'];

    function ProductsSearch($resource) {
        var resourceUrl =  'api/_search/products/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
