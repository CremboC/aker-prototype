$(document).ready(function () {
    $('.jqp-samples').each(function () {
        var $this = $(this);

        var id = $this.find('.jqp-append-before').attr('id');
        id = id.split("-")[1];

        $(this).pager({
            url: '/samples/byGroup/' + id,
            template: '#sample-template',
            loadButton: '#load-' + id,
            appendBefore: '#append-' + id,
            metadata: '#meta-' + id
        });
    });
});
