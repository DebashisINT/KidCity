package com.kcteam.features.chat.presentation

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.chat.api.ChatRepoProvider
import com.kcteam.features.chat.model.ChatListDataModel
import com.kcteam.features.chat.model.ChatListResponseModel
import com.kcteam.features.chat.model.ChatUserDataModel
import com.kcteam.features.chat.model.GroupUserDataModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.Serializable

class ChatListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rv_chat_list: RecyclerView
    private lateinit var et_msg: AppCustomEditText
    private lateinit var tv_send_btn: AppCustomTextView
    private lateinit var rl_chat_main: RelativeLayout

    private var chatAdapter: ChatListAdapter?= null
    var toID = ""
    private var chatUser: ChatUserDataModel?= null
    private var newUser: GroupUserDataModel?= null
    private var isGroup: Boolean = false

    //For Pagination
    private var pageSize = 10
    private var pageNo = 1
    private var previousTotalItemCount = 0
    private var firstVisibleItem: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private val startingPageIndex = 0
    private var currentPage = 0
    private val visibleThreshold = 5
    private var isOnPagination = false;
    private var loading = true

    private val chatList: ArrayList<ChatListDataModel> by lazy {
        ArrayList<ChatListDataModel>()
    }

    companion object {
        fun newInstance(user: Any): ChatListFragment {
            val fragment = ChatListFragment()

            val bundle = Bundle()
            bundle.putSerializable("user", user as Serializable?)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        if (arguments?.getSerializable("user") is ChatUserDataModel) {
            chatUser = arguments?.getSerializable("user") as ChatUserDataModel?
            toID = chatUser?.id!!
            isGroup = chatUser?.isGroup!!
        }
        else {
            newUser = arguments?.getSerializable("user") as GroupUserDataModel?
            toID = newUser?.id!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        initView(view)
        initClickListener()

        if (AppUtils.isOnline(mContext)) {
            pageNo = 1
            isOnPagination = false
            previousTotalItemCount = 0
            loading = true
            getChatList()
        }
        else
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

        return view
    }

    private fun initView(view: View) {
        view.apply {
            rv_chat_list = findViewById(R.id.rv_chat_list)
            progress_wheel = findViewById(R.id.progress_wheel)
            et_msg = findViewById(R.id.et_msg)
            tv_send_btn = findViewById(R.id.tv_send_btn)
            rl_chat_main = findViewById(R.id.rl_chat_main)
        }
        progress_wheel.stopSpinning()

        rv_chat_list.apply {
            val linearLayoutManager = LinearLayoutManager(mContext)
            linearLayoutManager.also {
                it.stackFromEnd = true
                layoutManager = it
            }

            chatAdapter = ChatListAdapter(mContext, isGroup) {
                isOnPagination = true
                getChatList()
            }
            addItemDecoration((StickyRecyclerHeadersDecoration(chatAdapter)))
            adapter = chatAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val mLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (dy < 0) {
                        visibleItemCount = recyclerView.childCount
                        totalItemCount = mLayoutManager.itemCount
                        firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()
                        //onScroll()
                        /*if (!loading) {
                            if (visibleItemCount + firstVisibleItem >= totalItemCount
                                    && firstVisibleItem >= 0
                                    && totalItemCount >= 10) {
                                loading = true
                                isOnPagination = true
                                getChatList()

                            }

                        }    */
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    }
                }
            })
        }
    }

    private fun onScroll() {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
            currentPage++
        }

        // If it isn’t currently loading, we check to see if we have reached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && /*totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold*/ visibleItemCount + firstVisibleItem >= totalItemCount
                && firstVisibleItem >= 0
                && totalItemCount >= 10) {
            loading = true
            isOnPagination = true
            //pageSize = pageSize + 10;
            if (AppUtils.isOnline(mContext)) {
                pageNo += 1
                getChatList()
            }
            else {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                previousTotalItemCount = 0
            }
        }
    }

    private fun initClickListener() {
        rl_chat_main.setOnClickListener(null)
        tv_send_btn.setOnClickListener(this)
    }

    private fun getChatList() {
        progress_wheel.spin()
        val repository = ChatRepoProvider.provideChatRepository()
        BaseActivity.compositeDisposable.add(
                repository.getChatList(toID, pageNo.toString(), pageSize.toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ChatListResponseModel
                            XLog.d("Get Chat List STATUS: " + response.status)
                            //loading = false
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                pageNo++
                                //chatList.addAll(0, response.chat_list!!)
                                if (!isOnPagination) {
                                    chatAdapter?.refreshList(response.chat_list!!)
                                    //rv_chat_list.scrollToPosition(chatAdapter?.chatList!!.size - 1)
                                }
                                else
                                    chatAdapter?.refreshListForPagination(response.chat_list!!)

                                val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                response.chat_list?.forEach {
                                    notificationManager.cancel((toID + "#" + it.id).hashCode())
                                }
                            }
                            else if (response.status == NetworkConstant.NO_DATA) {
                            }
                            else {
                                if (!isOnPagination)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }
                            isOnPagination = false
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            /*loading = false
                            isOnPagination = false*/
                            if (error != null)
                                XLog.d("Get Chat List ERROR: " + error.localizedMessage)

                            if (!isOnPagination)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            isOnPagination = false
                        })
        )
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_send_btn -> {
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                if (TextUtils.isEmpty(et_msg.text.toString().trim()))
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_msg))
                else
                    sendChat()
            }
        }
    }

    private fun sendChat() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val time = AppUtils.getCurrentISODateTime()
        val msgID = Pref.user_id + "_" + toID + "_" + System.currentTimeMillis()

        progress_wheel.spin()
        val repository = ChatRepoProvider.provideChatRepository()
        BaseActivity.compositeDisposable.add(
                repository.sendChat(msgID, AppUtils.encodeEmojiAndText(et_msg.text.toString().trim()), toID, time)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("Send Chat STATUS: " + response.status)
                            if (response.status == NetworkConstant.SUCCESS) {
                                (mContext as DashboardActivity).isRefreshChatUserList = true
                                progress_wheel.stopSpinning()
                                //chatList.add(ChatListDataModel(msgID, et_msg.text.toString().trim(), time, Pref.user_id!!, Pref.user_name!!))
                                chatAdapter?.refreshListWithOneMsg(ChatListDataModel(msgID, et_msg.text.toString().trim(), time, Pref.user_id!!, Pref.user_name!!, "Delivered"))
                                rv_chat_list.smoothScrollToPosition(chatAdapter?.chatList!!.size - 1)
                                et_msg.setText("")
                            }
                            else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            if (error != null)
                                XLog.d("Send Chat ERROR: " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    fun updateUi(intent: Intent) {
        //chatList.add(intent.getSerializableExtra("chatData") as ChatListDataModel)
        chatAdapter?.refreshListWithOneMsg(intent.getSerializableExtra("chatData") as ChatListDataModel)
        rv_chat_list.smoothScrollToPosition(chatAdapter?.chatList!!.size - 1)
        (mContext as DashboardActivity).isRefreshChatUserList = true

        updateStatusApi()
    }

    private fun updateStatusApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = ChatRepoProvider.provideChatRepository()
        BaseActivity.compositeDisposable.add(
                repository.updateChatStatus(toID)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("Send Chat STATUS: " + response.status)
                            if (response.status == NetworkConstant.SUCCESS) {
                                progress_wheel.stopSpinning()
                            }
                            else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            if (error != null)
                                XLog.d("Send Chat ERROR: " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    fun updateStatus() {
        chatAdapter?.chatList?.forEach {
            it.status = "Read"
        }
        chatAdapter?.notifyDataSetChanged()
    }
}