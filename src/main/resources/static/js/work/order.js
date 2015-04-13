/**
 * For WorkController@order
 */
$(document).ready(function () {
    var $project = $('#project-search');
    var searchQuery;
    var formGroup = $('#form-group-template').handlebars();
    var $submit = $('.submit');

    $project.find('button').on('click', function (e) {
        e.preventDefault();

        var projectCode = $project.find('input').val();

        searchQuery = $.ajax({
            url: 'http://localhost:8082/authorities/search/by-type-and-code',
            data: {
                type: $('.product-name').val(),
                code: projectCode
            }
        });

        searchQuery.then(function (data) {
            var html;

            console.log(data);
            if (!data) {
                return;
            }

            var quantity = data._embedded.purchasingAuthorities[0].quantity;
            var sampleCount = $('#sample-count').val();

            var remaining = quantity - sampleCount;

            if (remaining > 0) {
                html = formGroup({
                    text: 'Valid order. Credit remaining after order: ' + remaining,
                    success: true
                });
                $submit.removeAttr('disabled');
                $project.addClass('has-success');
                $project.find('.input-group-addon').css({
                    color: 'green'
                }).toggleClass('hidden');
            } else {
                html = formGroup({
                    text: 'Insufficient amount of credit.',
                    success: false
                });
            }
            $('.project-code').append(html);

        });
    });

    $('#submit-ws').on('click', function (e) {
        e.preventDefault();

        var $self = $(this);
        var submitQuery;

        var attr = $self.attr('disabled');

        if (!attr) {
            var form = $('form').serializeArray();

            for (var i = 0; i < form.length; i++) {
                if (form[i].name == '_method') {
                    form[i].value = 'post';
                    break;
                }
            }

            $.ajax({
                url: '/work/update',
                data: form,
                method: 'post'
            }).then(function (data) {

                submitQuery = $.ajax({
                    url: 'http://localhost:8083/orders/create/',
                    data: form,
                    method: 'post'
                });

                submitQuery.then(function (data) {
                    console.log(data);
                    if (data.processed) {
                        var template = $('#submitted-template').handlebars();
                        $(".form-wrapper").html('').html(template({
                            text: 'Order Submitted'
                        }));

                        $.ajax({
                            url: '/work/clear'
                        });
                    }
                }, function (a, b, c) {
                    console.log(a);
                    console.log(b);
                    console.log(c);
                });
            });

        }

    });
});
