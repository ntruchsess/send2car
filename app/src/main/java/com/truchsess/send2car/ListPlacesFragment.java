package com.truchsess.send2car;

import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

/**********************************************************************************************
 Copyright (C) 2020 Norbert Truchsess norbert.truchsess@t-online.de

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************************/
public class ListPlacesFragment extends ListFragment {

    private Listener listener;

    public interface Listener {
        void onListItemClick(int position);
    }

    public void setListPlacesListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (listener != null) {
            listener.onListItemClick(position);
        }
    }
}
