/**
 * Created by pi1 on 17/02/2015.
 */

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

    // increase the page number, makes sure
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

    // default method upon successful query
    var success = settings.success ? settings.success :
        function (data) {
            if (data.content.length > 0) {

                totalPages = data.totalPages;
                isLastPage = data.last;

                increasePage();

                var htmlArray = $.map($.makeArray(data.content), function (sample, i) {
                    return template(sample);
                });

                var htmlString = $.map(htmlArray, function (h) {
                    return h;
                });

                $appendBefore.before(htmlString);
                updateLoaderButton();

                var currentPage = $loadButton.data('page');
                if (currentPage >= totalPages) {
                    $loadButton.attr("disabled", "disabled");
                }
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

$(document).ready(function () {

    $('#jqp-samples').pager({
        url: '/samples/json/',
        template: '#sample-template',
        loadButton: '#jqp-load-more-samples',
        appendBefore: '#jqp-append-before-samples',
        preload: true
    });

    $('#jqp-groups').pager({
        url: '/groups/json/',
        template: '#sample-template',
        loadButton: '#jqp-load-more-groups',
        appendBefore: '#jqp-append-before-groups',
        preload: true
    });

});