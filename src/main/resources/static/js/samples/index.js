/**
 * Created by pi1 on 17/03/2015.
 */
$(document).ready(function () {

    var $samples = $('#jqp-samples');

    $samples.pager({
        url: '/samples/json/',
        template: '#sample-template',
        loadButton: '#jqp-load-more-samples',
        appendBefore: '#jqp-append-before-samples',
        scrollLoad: true
    });

    $samples.selectableElement({
        element: 'tbody .selectable',
        singleType: true
    });

    $('#create-group').createGroup({
        modal: '#groupModal',
        confirm: '#modalConfirm',
        template: '#modal-sample-template',
        mode: 'samples'
    });

    $samples.on('element.selected', function (event) {
        var $button = $('#create-group');
        if (event.count > 0) {
            $button.removeAttr('disabled');
        } else {
            $button.attr('disabled', 'disabled');
        }
    });
});