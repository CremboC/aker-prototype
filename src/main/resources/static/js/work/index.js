/**
 * Created by pi1 on 17/03/2015.
 */
$(document).ready(function () {

    var $workForm = $('#work-form'),
        $samples = $('.jqp-samples'),
        $groups = $('.jqp-groups'),
        $products = $('#products'),
        $submit = $('#submit');

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    // compile template using Handlebars
    var optionTemplate = Handlebars.compile($('#option-template').html()),
        productTemplate = Handlebars.compile($('#select-template').html());

    $groups.selectableElement({
        element: 'tbody .selectable'
    });

    $samples.selectableElement({
        element: 'tbody .selectable'
    });

    var productQuery = $.ajax('http://localhost:8081/products/');
    var products;

    function updateOptions(product) {
        var optionsHtml = '';

        if (!product._links.options) {
            return;
        }

        var optionsQuery = $.ajax(product._links.options.href);
        optionsQuery.then(function (data) {
            var options = data._embedded.options;
            product.options = options;
            $.each(options, function (index, option) {

                // has no options
                if (option.perSample) {
                    return;
                }

                var context = {
                    name: option.name,
                    options: stringByCommasToArray(option.restrictedOptions),
                    required: option.required
                };

                optionsHtml += optionTemplate(context);
            });

            $('.options').html('').html(optionsHtml);
        });
    }

    productQuery.then(function (data) {
        $workForm.find('.hidden').toggleClass('hidden');
        $workForm.find('.show').remove();

        products = data._embedded.products;
        var sampleHtml = '';
        var samplesPager, groupsPager;

        $.each(products, function (index, product) {
            product.index = index;
            sampleHtml += productTemplate(product);
        });

        $products.append(sampleHtml);

        $products.on('change', function (e) {
            var $selected = $(this).find(":selected"),
                valueSelected = $selected.data('index');

            if (valueSelected === '') {
                return;
            }

            var validTypes = [];

            $.each(products[valueSelected].inputMaterial, function (index, material) {
                validTypes.push(material.type.value);
            });

            updateOptions(products[valueSelected]);

            var payload = {
                data: {
                    types: validTypes.join(',')
                }
            };

            samplesPager.load(payload);
            groupsPager.load(payload);
        });

        samplesPager = $samples.pager({
            url: '/samples/byTypes',
            template: '#sample-template',
            loadButton: '#load-samples',
            appendBefore: '#append-samples',
            metadata: '#metadata-samples',
            scrollLoad: true,
            preload: false
        });

        groupsPager = $groups.pager({
            url: '/groups/byTypes',
            template: '#group-template',
            loadButton: '#load-groups',
            appendBefore: '#append-groups',
            metadata: '#metadata-groups',
            scrollLoad: false,
            preload: false
        });

        $submit.on('click', function (e) {
            e.preventDefault();

            var order = {
                product: [],
                samples: [],
                groups: [],
                options: []
            };

            var form = $('form').serializeArray();

            $.each(form, function (index, input) {
                switch (input.name) {
                    case 'product':
                        var productIndex = input.value.split('$')[0];

                        order.product = {
                            name: products[productIndex].name,
                            unitCost: products[productIndex].unitCost,
                            options: products[productIndex].options
                        };

                        var perSampleOptions = $.grep(order.product.options, function (opt) {
                            return opt.perSample;
                        });

                        order.product.options = [];

                        $.each(perSampleOptions, function (i, opt) {
                            order.product.options.push({
                                name: opt.name,
                                value: "",
                                restrictedOptions: stringByCommasToArray(opt.restrictedOptions)
                            });
                        });
                        break;

                    case 'samples':
                        if (input.value !== "") {
                            order.samples.push({
                                barcode: input.value
                            });
                        }
                        break;

                    case 'groups':
                        order.groups.push(input.value);
                        break;

                    case 'options':
                        var split = input.value.split('$');
                        order.options.push({
                            name: split[0],
                            value: split[1]
                        });
                        break;
                }
            });

            var orderQuery = $.ajax('/work/order', {
                method: $('form').attr('method'),
                data: JSON.stringify(order),
                processData: false,
                contentType: 'application/json',
                beforeSend: function (request) {
                    request.setRequestHeader(header, token);
                }
            });

            orderQuery.then(function (data) {
                if (data) {
                    window.location.href = "/work/order";
                }
            });
        });

    }, function (xhr, status, errorThrown) {
        console.log(xhr);
        console.log(status);
        console.log(errorThrown);
    });

});