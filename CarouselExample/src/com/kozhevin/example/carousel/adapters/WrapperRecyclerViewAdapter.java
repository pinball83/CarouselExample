package com.kozhevin.example.carousel.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kozhevin.example.carousel.R;

public class WrapperRecyclerViewAdapter<T extends ViewHolder> extends RecyclerView.Adapter<ViewHolder> {

	private final static String		TAG						= "WrapperRecyclerViewAdapter";

	private static final boolean	DEBUG					= false;

	private final static int		TYPE_WRAPPER_START		= 0;
	private final static int		TYPE_WRAPPER_FINISH		= 2;
	private final static int		TYPE_WRAPPED			= 1;

	private final static int		OFFSET_WITH_START_ITEM	= 1;
	private final static int		OFFSET_FULL				= 2;

	private RecyclerView.Adapter<T>	mAdapter;
	private AdapterDataObserver		mInternalObserver;


	public WrapperRecyclerViewAdapter() {
		mInternalObserver = new AdapterDataObserver() {

			@Override
			public void onChanged() {
				notifyDataSetChanged();
				if (DEBUG) {
					Log.v("Carousel", "Wrapper onChanged");
				}
			}


			@Override
			public void onItemRangeChanged(int positionStart, int itemCount) {
				notifyItemRangeChanged(positionStart + getOffsetPositionForAnimations(), itemCount);
			}


			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				notifyItemRangeInserted(positionStart + getOffsetPositionForAnimations(), itemCount);
			}


			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				notifyItemRangeRemoved(positionStart + getOffsetPositionForAnimations(), itemCount);
				if (DEBUG) {
					Log.v("Carousel", "Wrapper removed: posFromWrapped = " + positionStart + ", mypos = "
							+ (positionStart + getOffsetPositionForAnimations()) + ", itemCount = " + itemCount);
				}
			}
		};
	}


	@SuppressWarnings("unchecked")
	public void setAdaper(Adapter<? extends ViewHolder> pAdapter) {
		if (mAdapter != null) {
			mAdapter.unregisterAdapterDataObserver(mInternalObserver);
		}
		mAdapter = (Adapter<T>)pAdapter;
		mAdapter.registerAdapterDataObserver(mInternalObserver);
	}


	@Override
	public int getItemCount() {
		return mAdapter.getItemCount() + OFFSET_FULL;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void onBindViewHolder(ViewHolder pHolder, int pPosition) {

		if (getItemViewType(pPosition) == TYPE_WRAPPED) {
			mAdapter.onBindViewHolder((T)pHolder, pPosition - OFFSET_WITH_START_ITEM);
		}

	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup pHolder, int pType) {

		if (DEBUG) {
			Log.d(TAG, "CreateViewHolder with type =" + pType);
		}

		if (pType == TYPE_WRAPPER_START) {
			View v = LayoutInflater.from(pHolder.getContext()).inflate(R.layout.wrapper_item, pHolder, false);
			WrapperViewHolderStart lWrapperViewHolderStart = new WrapperViewHolderStart(v);
			return lWrapperViewHolderStart;
		}

		if (pType == TYPE_WRAPPER_FINISH) {
			View v = LayoutInflater.from(pHolder.getContext()).inflate(R.layout.wrapper_item, pHolder, false);
			return new WrapperViewHolderFinish(v);
		}

		return mAdapter.onCreateViewHolder(pHolder, pType);

	}


	@Override
	public int getItemViewType(int pPosition) {

		if (pPosition == (mAdapter.getItemCount() + OFFSET_WITH_START_ITEM)) {
			return TYPE_WRAPPER_FINISH;
		}

		if (pPosition == 0) {
			return TYPE_WRAPPER_START;
		}

		return TYPE_WRAPPED;

	}

	public static class WrapperViewHolderStart extends RecyclerView.ViewHolder {

		public WrapperViewHolderStart(View itemView) {
			super(itemView);
		}

	}

	public static class WrapperViewHolderFinish extends RecyclerView.ViewHolder {

		public WrapperViewHolderFinish(View itemView) {
			super(itemView);
		}

	}


	@Override
	public long getItemId(int pPosition) {
		return super.getItemId(pPosition);
	}


	@Override
	public void setHasStableIds(boolean pHasStableIds) {
		super.setHasStableIds(pHasStableIds);
	}


	public int getOffsetPositionForAnimations() {
		return OFFSET_WITH_START_ITEM;
	}


	public RecyclerView.Adapter<T> getAdaper() {
		return mAdapter;
	}
}
