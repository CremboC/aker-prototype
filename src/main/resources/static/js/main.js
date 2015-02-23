/**
 * Created by pi1 on 17/02/2015.
 */

Handlebars.registerHelper('list', function (context, options) {
    var ret = "<ul class='list-unstyled'>";

    for (var i = 0, j = context.length; i < j; i++) {
        ret = ret + "<li>" + options.fn(context[i]) + "</li>";
    }

    return ret + "</ul>";
});

$.fn.pager = function (options) {
    var defaults = {
        template: '#entry-template',
        loadButton: '.jqp-load-more',
        appendBefore: '.jqp-append-before',
        preload: true,
        url: '',
        dataType: 'json',
        type: 'GET'
    };

    var settings = $.extend({}, defaults, options);

    // cachce all important variables and selectors
    var $paginate = $(this),
        $template = $(settings.template),
        $loadButton = $(settings.loadButton),
        $appendBefore = $(settings.appendBefore);

    // fetch original load button text so we can restore it after changing it to "Loading"
    var originalLoadButtonText = $loadButton.html();

    // will be changed on first load
    var totalPages = 0,
        isLastPage = false;

    // set first page
    $loadButton.data('page', 0);

    // compile template using Handlebars
    var source = $template.html();
    var template = Handlebars.compile(source);

    // increase the page number, makes sure one cannot load more pages than there are in total
    var increasePage = function () {
        var currentPage = parseInt($loadButton.data('page'));

        if (currentPage + 1 > totalPages) {
            $loadButton.hide();
        } else {
            $loadButton.data('page', currentPage + 1);
        }
    };

    // update the loader button, changing between "loading" and normal state
    var updateLoaderButton = function () {
        var text = $loadButton.html();

        if (originalLoadButtonText == text) {
            $loadButton.html("<span class='glyphicon glyphicon-refresh glyphicon-spin'></span> Loading ...");
            $(this).attr('disabled', 'disabled');
        } else {
            $loadButton.html(originalLoadButtonText);
            $loadButton.removeAttr('disabled');
        }
    };

    // after loading the final page, cleanup
    var cleanup = function () {
        $(this).attr('disabled', 'disabled');
        $loadButton.remove();
    };

    // default method upon successful query
    var success = settings.success ? settings.success :
        function (data) {
            if (data.content.length > 0) {
                // get total pages from Spring's Pageable
                totalPages = data.totalPages;
                // get the last page
                isLastPage = data.last;

                increasePage();

                // convert the json content into html using the template
                var sampleHtml = $.map($.makeArray(data.content), function (sample) {
                    return template(sample);
                });

                $appendBefore.before(sampleHtml);
                updateLoaderButton();

                var currentPage = $loadButton.data('page');
                if (currentPage >= totalPages) {
                    cleanup();
                }
            } else {
                cleanup();
            }
        };

    // default method upon error
    var error = settings.error ? settings.error :
        function (xhr, status, errorThrown) {
            console.log(xhr);
            console.log(status);
            console.log(errorThrown);
        };

    // wrapper to $.ajax
    var load = function () {
        $.ajax(settings.url, {
            dataType: settings.dataType,
            type: settings.type,
            data: {
                page: $loadButton.data('page'),
                size: 10
            },
            success: success,
            error: error
        }).then(function () {
            $paginate.trigger('pager.loaded')
        });
    };

    if (!isLastPage) {
        $loadButton.on('click', function (e) {
            e.preventDefault();
            updateLoaderButton();
            load();
        });
    }

    // immediately load first page
    if (settings.preload) {
        updateLoaderButton();
        load();
    }

    return this;
};

$.fn.modalForm = function (options) {

    var defaults = {
        modal: '',
        confirm: '',
        form: 'form'
    };

    var settings = $.extend({}, defaults, options);

    var $caller = $(this),
        $modal = $(settings.modal),
        $form = $(settings.form),
        $confirmButton = $(settings.confirm),
        $template = $('#modal-sample-template');

    // compile template using Handlebars
    var source = $template.html();
    var template = Handlebars.compile(source);

    $caller.on('click', function (e) {
        e.preventDefault();

        var $tbody = $modal.find('tbody');
        var samples = [];
        var serializedForm = $form.serializeArray();

        $.each(serializedForm, function (index, checkbox) {
            var $row = $('#' + checkbox.value);

            var sample = {};

            sample.barcode = checkbox.value;
            sample.name = $row.find('.name').text();
            sample.type = $row.find('.type').text();
            sample.status = $row.find('.status').text();

            samples.push(sample);
        });

        var sampleHtml = $.map(samples, function (sample) {
            return template(sample);
        });

        $tbody.append(sampleHtml);

        $modal.modal();
    });

    return this;
};

$.fn.selectableElement = function (options) {

    var defaults = {
        element: ''
    };

    var settings = $.extend({}, defaults, options);

    var $wrapper = $(this);

    console.log($wrapper.attr('id'));

    console.log('#' + $wrapper.attr('id') + ' ' + settings.element);

    $wrapper.on('click', '#' + $wrapper.attr('id') + ' ' + settings.element, function (e) {
        e.preventDefault();
        console.log($(this));
        var $checkbox = $(this).find('input[type="checkbox"]');

        $checkbox.prop("checked", !$checkbox.prop("checked"));
    });

    return this;
};

$(document).ready(function () {

    var $samples = $('#jqp-samples');

    $samples.pager({
        url: '/samples/json/',
        template: '#sample-template',
        loadButton: '#jqp-load-more-samples',
        appendBefore: '#jqp-append-before-samples',
        preload: true
    });

    //$samples.on('pager.loaded', function () {
    $samples.selectableElement({
        element: 'tr',
        updateOn: 'pager.loaded'
    });
    //});

    $('#jqp-groups').pager({
        url: '/groups/json/',
        template: '#sample-template',
        loadButton: '#jqp-load-more-groups',
        appendBefore: '#jqp-append-before-groups',
        preload: true
    });

    $('#create-group').modalForm({
        modal: '#groupModal',
        confirm: '#modalConfirm'
    });

});