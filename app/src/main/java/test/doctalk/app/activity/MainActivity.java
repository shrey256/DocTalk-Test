package test.doctalk.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import test.doctalk.app.R;
import test.doctalk.app.adapter.CardAdapter;
import test.doctalk.app.model.Github;
import test.doctalk.app.service.GithubService;
import test.doctalk.app.service.ServiceFactory;
import test.doctalk.app.utils.VerticalSpaceItemDecoration;

public class MainActivity extends AppCompatActivity {

    GithubService service;
    Subscription subscription;
    CardAdapter mCardAdapter;
    EditText queryText;
    LinearLayoutManager layoutManager;
    int visibleItemCount = 0;
    int totalItemCount = 0;
    int lastVisibleItem = 0;
    boolean loading = true;
    int nextPage = 1;
    private final int PAGE_ONE = 1;
    private final int MIN_SEARCH_LENGTH = 2;
    private final int LIST_VERTICAL_SPACING = 25;
    private final int PAGE_SIZE = 30;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mCardAdapter = new CardAdapter(getApplicationContext());
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(LIST_VERTICAL_SPACING));
        mRecyclerView.setAdapter(mCardAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findFirstVisibleItemPosition();

                    if (loading){
                        if ((visibleItemCount + lastVisibleItem) >= totalItemCount){
                            loading = false;
                            gitHubApiCall(queryText.getText().toString(), nextPage);
                        }
                    }
                }
            }
        });


        service = ServiceFactory.createRetrofitService(GithubService.class, GithubService.SERVICE_ENDPOINT);


        queryText = (EditText) findViewById(R.id.query);
        queryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(subscription != null && !subscription.isUnsubscribed()){
                    subscription.unsubscribe();
                }
                if(charSequence.length() > MIN_SEARCH_LENGTH){
                    gitHubApiCall(charSequence.toString(), PAGE_ONE);
                }
                else {
                    mCardAdapter.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        queryText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    findViewById(R.id.search).setAlpha(0.5f);
                }
                else{
                    findViewById(R.id.search).setAlpha(1f);
                }
            }
        });



        ImageView clearButton = (ImageView) findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryText.setText("");
            }
        });

    }

    private void gitHubApiCall(String query, final int page){
        subscription = service.getUsers(query, "followers", PAGE_SIZE, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Github>() {
                    @Override
                    public final void onCompleted() {

                    }

                    @Override
                    public final void onError(Throwable e) {

                    }

                    @Override
                    public final void onNext(Github response) {
                        loading = true;
                        totalItemCount = response.getTotal_count();
                        if(page == 1) {
                            mCardAdapter.clear();
                        }
                        mCardAdapter.addData(response);
                        nextPage = page + 1;
                    }
                });
    }
}
