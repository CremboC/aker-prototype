/**
 * Created by pi1 on 18/03/2015.
 */
$(document).ready(function () {
    var ordersQuery;
    var template = $('#order-template').handlebars(),
        $append = $('.append'),
        id = $('input[name=id]').val();

    ordersQuery = $.ajax({
        url: '/orders/json/' + id,
        dataType: 'json'
    });

    ordersQuery.then(function (order) {
        console.log(order);

        var created = new Date(order.created);
        order.created = created.getDate() + "/" + (created.getMonth() + 1) + "/" + created.getFullYear() + " " + created.getHours() + ":" + created.getMinutes();

        $append.html('').html(template(order));
    }, function (xhr, status, error) {
        $append.html('').html('<span class="glyphicon glyphicon-warning-sign"></span>Server problem...')
    });

    $append.on('click', '[data-extend]', function(e) {
        e.preventDefault();

        var $this = $(this),
            $companion = $($this.data('extend'));

        $companion.toggleClass('hidden');
        $this.find('.glyphicon').toggleClass('glyphicon-menu-down').toggleClass('glyphicon-menu-up');
    })
});