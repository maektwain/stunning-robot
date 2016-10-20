(function(){
      'use strict';
      angular
            .module('frontendApp')
            .factory('OauthApps',OauthApps);


      OauthApps.$inject = ['$resource'];

      function OauthApps ($resource){

            var resourceUrl = 'api/account/apps';

            return $resource(resourceUrl, {}, {

                   'get': {

                        method: 'GET',
                        transformResponse: function (data) {
                                if (data){
                                    data = angular.fromJson(data);
                                }
                                return data;
                        }
                   }

            });
      }


})();
