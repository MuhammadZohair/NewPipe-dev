package org.schabi.newpipe.util;

import android.content.Context;

import androidx.fragment.app.Fragment;

import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.local.dialog.PlaylistAppendDialog;
import org.schabi.newpipe.player.playqueue.SinglePlayQueue;

import java.util.Collections;

public enum StreamDialogEntry {
    //////////////////////////////////////
    // enum values with DEFAULT actions //
    //////////////////////////////////////

    enqueue_on_background(R.string.enqueue_on_background, (fragment, item) ->
            NavigationHelper.enqueueOnBackgroundPlayer(fragment.getContext(), new SinglePlayQueue(item), false)),

    enqueue_on_popup(R.string.enqueue_on_popup, (fragment, item) ->
            NavigationHelper.enqueueOnPopupPlayer(fragment.getContext(), new SinglePlayQueue(item), false)),

    start_here_on_background(R.string.start_here_on_background, (fragment, item) ->
            NavigationHelper.playOnBackgroundPlayer(fragment.getContext(), new SinglePlayQueue(item), true)),

    start_here_on_popup(R.string.start_here_on_popup, (fragment, item) ->
            NavigationHelper.playOnPopupPlayer(fragment.getContext(), new SinglePlayQueue(item), true)),

    set_as_playlist_thumbnail(R.string.set_as_playlist_thumbnail, (fragment, item) -> {
    }), // has to be set manually

    delete(R.string.delete, (fragment, item) -> {
    }), // has to be set manually

    append_playlist(R.string.append_playlist, (fragment, item) -> {
        if (fragment.getFragmentManager() != null) {
            PlaylistAppendDialog.fromStreamInfoItems(Collections.singletonList(item))
                    .show(fragment.getFragmentManager(), "StreamDialogEntry@append_playlist");
        }
    }),

    share(R.string.share, (fragment, item) ->
            ShareUtils.shareUrl(fragment.getContext(), item.getName(), item.getUrl()));


    ///////////////
    // variables //
    ///////////////

    private static StreamDialogEntry[] enabledEntries;
    private final int resource;
    private final StreamDialogEntryAction defaultAction;
    private StreamDialogEntryAction customAction;

    StreamDialogEntry(final int resource, StreamDialogEntryAction defaultAction) {
        this.resource = resource;
        this.defaultAction = defaultAction;
        this.customAction = null;
    }


    ///////////////////////////////////////////////////////
    // non-static methods to initialize and edit entries //
    ///////////////////////////////////////////////////////

    /**
     * To be called before using {@link #setCustomAction(StreamDialogEntryAction)}
     */
    public static void setEnabledEntries(StreamDialogEntry... entries) {
        // cleanup from last time StreamDialogEntry was used
        for (StreamDialogEntry streamDialogEntry : values()) {
            streamDialogEntry.customAction = null;
        }

        enabledEntries = entries;
    }

    public static String[] getCommands(Context context) {
        String[] commands = new String[enabledEntries.length];
        for (int i = 0; i != enabledEntries.length; ++i) {
            commands[i] = context.getResources().getString(enabledEntries[i].resource);
        }

        return commands;
    }


    ////////////////////////////////////////////////
    // static methods that act on enabled entries //
    ////////////////////////////////////////////////

    public static void clickOn(int which, Fragment fragment, StreamInfoItem infoItem) {
        if (enabledEntries[which].customAction == null) {
            enabledEntries[which].defaultAction.onClick(fragment, infoItem);
        } else {
            enabledEntries[which].customAction.onClick(fragment, infoItem);
        }
    }

    /**
     * Can be used after {@link #setEnabledEntries(StreamDialogEntry...)} has been called
     */
    public void setCustomAction(StreamDialogEntryAction action) {
        this.customAction = action;
    }

    public interface StreamDialogEntryAction {
        void onClick(Fragment fragment, final StreamInfoItem infoItem);
    }
}
