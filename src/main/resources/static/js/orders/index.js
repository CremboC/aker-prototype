/**
 * Created by pi1 on 17/03/2015.
 */
$(document).ready(function () {
    var ordersQuery;
    var template = $('#order-template').handlebars(),
        $append = $('.append');

    ordersQuery = $.get('/orders/get/');

    ordersQuery.then(function (data) {
        console.log(data);
        var orders = new JsonHal(data, "orders");

        if (orders.present()) {

            var ordersHtml = $.map(orders.get(), function (orderObj) {
                var order = orderObj.data;

                return template(order);
            });

            $append.append(ordersHtml);
        }
    });
});