/**
 * regex converts 'Yes, No' to 'Yes,No' i.e. removes spaces after comma for split to work properly
 * also removes last comma (and whitespace) at the end of the string
 * @param string
 * @returns {Array}
 */
function stringByCommasToArray(string) {
    if (!string) {
        return [];
    }
    return string.replace(/[,\s]+/g, ',').replace(/[,\s]+$/g, '').split(',');
}

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

Handlebars.registerHelper('date', function (object) {
    var date = new Date(object);
    return date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes();
});

Handlebars.registerHelper('ifCond', function (v1, operator, v2, options) {

    switch (operator) {
        case '==':
            return (v1 == v2) ? options.fn(this) : options.inverse(this);
        case '===':
            return (v1 === v2) ? options.fn(this) : options.inverse(this);
        case '<':
            return (v1 < v2) ? options.fn(this) : options.inverse(this);
        case '<=':
            return (v1 <= v2) ? options.fn(this) : options.inverse(this);
        case '>':
            return (v1 > v2) ? options.fn(this) : options.inverse(this);
        case '>=':
            return (v1 >= v2) ? options.fn(this) : options.inverse(this);
        case '&&':
            return (v1 && v2) ? options.fn(this) : options.inverse(this);
        case '||':
            return (v1 || v2) ? options.fn(this) : options.inverse(this);
        default:
            return options.inverse(this);
    }
});


(function ($) {
    /**
     * Shortcut to Handlebars.compile(this.html());
     * @returns {*}
     */
    $.fn.handlebars = function () {
        return Handlebars.compile(this.html());
    };
})(jQuery);

(function ($) {
    $.fn.isAfter = function (sel) {
        return this.prevAll().filter(sel).length !== 0;
    };

    $.fn.isBefore = function (sel) {
        return this.nextAll().filter(sel).length !== 0;
    };
})(jQuery);

(function ($) {
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
})(jQuery);


(function ($) {

    /**
     * Asynchronously load paged data from a source
     *
     * @param options
     * @returns {$.fn}
     */
    $.fn.pager = function (options) {

        var defaults = {
            template: '#entry-template',
            loadButton: '', // .jqp-load-more
            appendBefore: '', // .jqp-append-before
            metadata: '', // .jqp-metadata
            preload: true,
            scrollLoad: false,
            loadEvents: 'click',
            url: '',
            dataType: 'json',
            type: 'GET',
            data: {}
        };

        var settings;

        settings = $.extend({}, defaults, options);

        // cache all important variables and selectors
        var $this = $(this),
            $template = $(settings.template),
            $loadButton = $(settings.loadButton),
            $appendBefore = $(settings.appendBefore),
            $metadata = $(settings.metadata);

        var appendClass = "jqp-appended";

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

        // after loading the final page, finish
        var finish = function () {
            $loadButton.attr('disabled', 'disabled');
            $loadButton.hide();
            $loadButton.data('page', 0);
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
                        return $(template(sample)).addClass(appendClass);
                    });

                    $appendBefore.before(sampleHtml);
                    updateLoaderButton();
                    updateMetadata(data);

                    var currentPage = $loadButton.data('page');
                    if (currentPage >= totalPages) {
                        finish();
                    }
                } else {
                    finish();
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
            var data = $.extend(settings.data, {
                page: $loadButton.data('page'),
                size: 20
            });

            $.ajax(settings.url, {
                dataType: settings.dataType,
                type: settings.type,
                data: data,
                success: success,
                error: error
            }).then(function () {
                $this.trigger('pager.loaded')
            });
        };

        $loadButton.on(settings.loadEvents, function (e) {
            e.preventDefault();
            if (!isLastPage) {
                updateLoaderButton();
                load();
            }
        });

        // immediately load first page
        if (settings.preload) {
            updateLoaderButton();
            load();
        }

        return {
            load: function (options) {
                var localDefaults = {
                    cleanup: true,
                    data: {}
                };

                var localSettings = $.extend({}, localDefaults, options);

                settings.data = localSettings.data;

                if (localSettings.cleanup) {
                    $metadata.html('');
                    $('.' + appendClass).remove();
                }

                load();
            },
            chain: this
        };
    };

}(jQuery));

/**
 * Makes and element which has a checkbox inside to be checkable by clicking anywhere on the element itself.
 *
 * @param options
 * @returns {$.fn}
 */
$.fn.selectableElement = function (options) {

    var defaults = {
        element: '',
        singleType: false
    };

    var settings = $.extend({}, defaults, options);

    var $wrapper = $(this),
        checkedCount = 0,
        previousType;

    var $lastSelected,
        displayWarning = false;

    /**
     *
     * @param $selectable the selectable
     * @param isMultiselect is user selecting multiple rows?
     */
    var select = function ($selectable, isMultiselect) {
        var $checkbox = $selectable.find('input[type="checkbox"]'),
            type = $checkbox.data('type');

        displayWarning = false;

        // user may click the checkbox itself, should still work
        if ($selectable.is('input[type="checkbox"]')) {
            return;
        }

        if (settings.singleType && checkedCount > 0) {
            if (type !== previousType) {
                displayWarning = true;
                return;
            }
        }

        $checkbox.prop("checked", !$checkbox.prop("checked"));
        $selectable.toggleClass("selected");

        if ($checkbox.prop("checked")) {
            checkedCount++;
        } else {
            checkedCount--;
        }

        previousType = type;

        $wrapper.trigger({
            type: 'element.selected',
            count: checkedCount
        });

        $lastSelected = $selectable;
    };

    $wrapper.on('click', settings.element, function (e) {
        var $this = $(this);

        var isMultiselect = e.ctrlKey || e.metaKey;

        if ($lastSelected && isMultiselect) {

            var selectAll = function (index, element) {
                select($(element), isMultiselect);
            };

            if ($this.isAfter($lastSelected)) {
                $lastSelected.nextUntil($this).add($this).each(selectAll);
            } else if ($this.isBefore($lastSelected)) {
                var toSelect = $lastSelected.prevUntil($this).add($this);

                if ($lastSelected.hasClass("selected")) {
                    toSelect.add($lastSelected);
                }

                toSelect.each(selectAll);

                $lastSelected = $this;
            }

        } else {
            select($this, isMultiselect);
        }

        if (displayWarning) {
            alert('Type must be the same!');
        }

        console.log($this);
        console.log($lastSelected);

    });

    return this;
};

/**
 * Helper to create a group or a labware
 *
 * @param options
 * @returns {$.fn}
 */
$.fn.group = function (options) {

    var defaults = {
        mode: '', // groups/samples
        modal: '', // modal selector
        template: '', // template selector
        confirm: '', // confirm button
        form: 'form',
        action: null
    };

    var settings = $.extend({}, defaults, options);

    var $caller = $(this),
        $modal = $(settings.modal),
        $form = $(settings.form),
        $confirmButton = $(settings.confirm),
        $template = $(settings.template),
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

    var properties = {
        samples: ['barcode', 'name', 'type', 'status'],
        groups: ['id', 'name', 'parent', 'count']
    };

    $caller.on('click', function (e) {
        e.preventDefault();
        cleanup();

        var elements = [];
        var serializedForm = $form.serializeArray();

        $.each(serializedForm, function (index, input) {
            if (!input.value || (input.name.indexOf("_") == 0)) {
                return;
            }

            var $row = $('#' + input.value);

            var element = {};

            $.each(properties[settings.mode], function (index, prop) {
                element[prop] = $row.find('.' + prop).text();
            });

            // override first element for simplicity
            element[properties[settings.mode][0]] = input.value;

            elements.push(element);
        });

        var html = $.map(elements, function (element) {
            return template(element);
        });

        $tbody.append(html);

        $modal.modal('show');
    });

    $modal.on('hide.bs.modal', function () {
        // cleanup modal on close
        // remove all samples from the table
        cleanup();
    });

    $confirmButton.on('click', function (e) {
        e.preventDefault();

        if (settings.action !== null) {
            $form.attr('action', settings.action);
        }

        $form.submit();
    });

    return this;
};

/**
 * Wrapper for dealing with JSON+HAL responses from a RESTful service
 *
 * @param data
 * @param object name of the embedded object
 * @constructor
 */
function JsonHal(data, object) {

    var empty = data ? false : true,
        embedded = empty ? [] : data._embedded;

    /**
     * Get the embedded data in this jsonHal query.
     *
     * @returns {Array}
     */
    this.get = function () {
        if (empty) {
            return []
        }

        var objects = [];
        for (var i = 0; i < embedded[object].length; i++) {
            var obj = embedded[object][i];

            var details = null;
            if ('_links' in obj && 'self' in obj['_links']) {
                details = obj['_links']['self'];
            }

            objects.push({
                data: obj,
                details: details,
                identifier: function () {
                    return this.data['_links']
                },
                link: function (child) {
                    if (!('_links' in this.data)) {
                        return null;
                    }

                    var childLinks = this.data['_links'][child];

                    if (!childLinks) {
                        return null;
                    }

                    return childLinks;
                }
            });
        }

        return objects;
    };

    /**
     * Get the metadata for this jsonHal query. i.e. the 'page' key in the response.
     * @returns {{size: number, totalElements: number, totalPages: number, number: number}}
     */
    this.meta = function () {
        if (empty || !('page' in data)) {
            return {
                size: 0,
                totalElements: null,
                totalPages: null,
                number: 0
            };
        }
        return data.page;
    };

    /**
     * Whether the data returned is empty. Recommend use is:
     * if (!varr.empty()) {
     *     // do something
     * }
     * @returns {boolean}
     */
    this.empty = function () {
        return empty;
    };

    /**
     * Reverse of this.empty()
     *
     * @returns {boolean}
     */
    this.present = function () {
        return !empty;
    };
}

$(document).ready(function () {
    $('[data-toggle="tooltip"]').tooltip();
    var $affix = $('.attached-affix');
    if ($affix.length !== 0) {
        $affix.data('offset-top', $affix.offset().top);
    }
});
