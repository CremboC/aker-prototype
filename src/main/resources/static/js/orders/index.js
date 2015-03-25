/**
 * Created by pi1 on 17/03/2015.
 */
$(document).ready(function () {
    var ordersQuery;
    var template = $('#order-template').handlebars(),
        $append = $('.append');

    ordersQuery = $.get('/orders/get/');

    ordersQuery.then(function (data) {
        var ordersHtml = $.map(data, function (order) {
            return template(order);
        });

        $append.append(ordersHtml);
    });
});