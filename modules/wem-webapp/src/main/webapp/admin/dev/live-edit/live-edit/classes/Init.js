(function( window ) {

    window.AdminLiveEdit = {};

    $liveedit( document ).ready( function() {
        if (document.location.href.indexOf('liveedit=false') > -1) {
            return;
        }

        // TODO: Reconsider this
        // Disable all A elements.
        // This is very cheap as we should disable JS events too. See if we can find another way.
        // To enable links again: $liveedit(document).off('click.liveEditDisableLinkElements');
        $liveedit( 'a' ).on( 'click.liveEditDisableLinkElements', function( event ) {
            event.preventDefault();
            return false;
        });

        var liveEdit = window.AdminLiveEdit;
        liveEdit.Windows.init();
        liveEdit.Regions.init();
        liveEdit.PageCanvas.init();
        liveEdit.Highlighter.init();
        liveEdit.PageElementSelector.init();
        liveEdit.Tooltip.init();
        liveEdit.DragDrop.init();
    } );

})( window );