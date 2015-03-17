/**
 * Created by pi1 on 17/03/2015.
 */
$(document).ready(function () {

    $('.jqp-samples').pager({
        url: '/samples/json/',
        template: '#sample-template',
        loadButton: '#jqp-load-more-samples',
        appendBefore: '#jqp-append-before-samples',
        metadata: '#jqp-metadata-samples'
    });

    $('.jqp-groups').pager({
        url: '/groups/json/',
        template: '#group-template',
        loadButton: '#load-groups',
        appendBefore: '#append-groups',
        metadata: '#metadata-groups'
    });

});