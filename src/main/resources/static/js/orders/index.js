/**
 * Created by pi1 on 17/03/2015.
 */
$(document).ready(function () {
    var ordersQuery;
    var template = $('#order-template').handlebars(),
        $append = $('.append');

    ordersQuery = $.ajax({
        url: '/orders/json/',
        dataType: 'json'
    });

    ordersQuery.then(function (data) {
        var ordersHtml = $.map(data, function (order) {
            var created = new Date(order.created);
            order.created = created.getDate() + "/" + (created.getMonth() + 1) + "/" + created.getFullYear() + " " + created.getHours() + ":" + created.getMinutes();
            return template(order);
        });

        $append.append(ordersHtml);
    });
});