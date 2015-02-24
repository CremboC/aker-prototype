/**
 * Created by pi1 on 17/02/2015.
 */

Handlebars.registerHelper('list', function (context, options) {
    if (context == undefined) {
        return;
    }

    var ret = "<ul class='list-unstyled'>";

    for (var i = 0, j = context.length; i < j; i++) {
        ret = ret + "<li>" + options.fn(context[i]) + "</li>";
    }

    return ret + "</ul>";
});

/**
 * Watches an element to see when it's visible on the screen. Triggers event 'on.screen' on the element once it's visible
 * @returns {$.fn}
 */
$.fn.watcher = function () {

    var $watch = $(this);

    var scrollTimeout;  // global for any pending scrollTimeout

    $(window).scroll(function () {
        if (scrollTimeout) {
            // clear the timeout, if one is pending
            clearTimeout(scrollTimeout);
            scrollTimeout = null;
        }
        scrollTimeout = setTimeout(scrollHandler, 250);
    });

    var triggerEvent = function () {
        $watch.trigger('on.screen');
    };

    var isScrolledIntoView = function ($elem) {
        var $window = $(window);

        var docViewTop = $window.scrollTop();
        var docViewBottom = docViewTop + $window.height();

        var elemTop = $elem.offset().top;
        var elemBottom = elemTop + $elem.height();

        return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
    };

    var init = function () {
        if (isScrolledIntoView($watch)) {
            triggerEvent();
        }
    };

    var scrollHandler = function () {
        if (isScrolledIntoView($watch)) {
            triggerEvent();
        }
    };

    init();

    return this;
};

/**
 * Asynchronously load data from a source
 *
 * @param options
 * @returns {$.fn}
 */
$.fn.pager = function (options) {
    var defaults = {
        template: '#entry-template',
        loadButton: '.jqp-load-more',
        appendBefore: '.jqp-append-before',
        metadata: '.jqp-metadata',
        preload: true,
        scrollLoad: false,
        loadEvents: 'click',
        url: '',
        dataType: 'json',
        type: 'GET'
    };

    var settings = $.extend({}, defaults, options);

    // cachce all important variables and selectors
    var $paginate = $(this),
        $template = $(settings.template),
        $loadButton = $(settings.loadButton),
        $appendBefore = $(settings.appendBefore),
        $metadata = $(settings.metadata);

    if (settings.scrollLoad) {
        settings.loadEvents += ' on.screen';

        $loadButton.watcher();
    }

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
            $loadButton.html("<span class='glyphicon glyphicon-refresh glyphicon-spin'></span> Loading");
            $loadButton.attr('disabled', 'disabled');
        } else {
            $loadButton.html(originalLoadButtonText);
            $loadButton.removeAttr('disabled');
        }
    };

    var updateMetadata = function (metadata) {
        var current = (metadata.number + 1) * metadata.numberOfElements;

        if (metadata.last) {
            current = metadata.totalElements;
        }

        $metadata.html("Showing " + current + " out of " + metadata.totalElements);
    };

    // after loading the final page, cleanup
    var cleanup = function () {
        $(this).attr('disabled', 'disabled');
        $loadButton.remove();
        $loadButton.off('click on.screen');
    };

    // default method upon successful query
    var success = settings.success ? settings.success :
        function (data) {
            console.log(data);
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
                updateMetadata(data);

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
                size: 20
            },
            success: success,
            error: error
        }).then(function () {
            $paginate.trigger('pager.loaded')
        });
    };

    if (!isLastPage) {
        $loadButton.on(settings.loadEvents, function (e) {
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

/**
 * Makes and element which has a checkbox inside to be checkable by clicking anywhere on the element itself.
 *
 * @param options
 * @returns {$.fn}
 */
$.fn.selectableElement = function (options) {

    var defaults = {
        element: ''
    };

    var settings = $.extend({}, defaults, options);

    var $wrapper = $(this),
        checkedCount = 0;

    $wrapper.on('click', settings.element, function (e) {
        var $checkbox = $(this).find('input[type="checkbox"]');

        // user may click the checkbox itself, should still work
        if (!$(this).is('input[type="checkbox"]')) {

        }

        console.log($(this));

        $checkbox.prop("checked", !$checkbox.prop("checked"));

        if ($checkbox.prop("checked")) {
            checkedCount++;
        } else {
            checkedCount--;
        }

        console.log(checkedCount);

        $wrapper.trigger({
            type: 'element.selected',
            count: checkedCount
        });
    });

    return this;
};

/**
 * Helper to create a group
 *
 * @param options
 * @returns {$.fn}
 */
$.fn.createGroup = function (options) {

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
        $template = $('#modal-sample-template'),
        $tbody = $modal.find('tbody');

    // compile template using Handlebars
    var source = $template.html();
    var template = Handlebars.compile(source);

    var cleanup = function () {
        $tbody.empty();
    };

    // don't show the modal immediately
    $modal.modal({
        show: false,
        keyboard: true
    });

    $caller.on('click', function (e) {
        e.preventDefault();
        cleanup();

        var samples = [];
        var serializedForm = $form.serializeArray();

        console.log(serializedForm);

        $.each(serializedForm, function (index, input) {
            console.log(input);
            if (!input.value || (input.name.indexOf("_") == 0)) {
                return;
            }
            console.log(input);

            var $row = $('#' + input.value);

            var sample = {};

            sample.barcode = input.value;
            sample.name = $row.find('.name').text();
            sample.type = $row.find('.type').text();
            sample.status = $row.find('.status').text();

            samples.push(sample);
        });

        var sampleHtml = $.map(samples, function (sample) {
            return template(sample);
        });

        $tbody.append(sampleHtml);

        $modal.modal('show');
    });

    $modal.on('hide.bs.modal', function () {
        // cleanup modal on close
        // remove all samples from the table
        cleanup();
    });

    $confirmButton.on('click', function (e) {
        e.preventDefault();
        $form.submit();
    });

    return this;
};