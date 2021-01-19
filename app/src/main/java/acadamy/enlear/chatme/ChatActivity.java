package acadamy.enlear.chatme;

import acadamy.enlear.chatme.adapter.ChatAdapter;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.query.Where;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.DataStoreException;
import com.amplifyframework.datastore.DataStoreItemChange;
import com.amplifyframework.datastore.generated.model.Message;
import com.amplifyframework.datastore.generated.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {

    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1. Set adapter
        ListView messageListView = findViewById(R.id.messageListView);

        chatAdapter = new ChatAdapter(getApplicationContext(), R.id.messageListView, messageArrayList);
        messageListView.setAdapter(chatAdapter);

        // 2. Get previous messages
        getPreviousMessages();
        // 3. Listen to new messages
        Amplify.DataStore.observe(
                Message.class,
                cancelable -> Log.i("Amplify-observer", "Observation began."),
                this::onNewMessageReceived,
                failure -> Log.e("MyAmplifyApp", "Observation failed.", failure),
                () -> Log.i("Amplify-observer", "Observation complete.")
        );
    }

    private void onNewMessageReceived(DataStoreItemChange<Message> messageChanged) {
        if (messageChanged.type().equals(DataStoreItemChange.Type.CREATE)) {
            Message message = messageChanged.item();
            messageArrayList.add(message);
            runOnUiThread(() -> chatAdapter.notifyDataSetChanged());
        }
    }

    private void getPreviousMessages() {
        Amplify.DataStore.query(
                Message.class,
                Where.sorted(Message.DATE.ascending()),
                (messages) -> runOnUiThread(() -> {
                    while (messages.hasNext()) {
                        Message message = messages.next();
                        messageArrayList.add(message);
                        chatAdapter.notifyDataSetChanged();
                    }
                }),
                Throwable::printStackTrace
        );
    }

    public void onClickSendMessage(View view) {
        // 1. Create
        EditText txtMessageContent = findViewById(R.id.txtMessageContent);
        String messageContent = txtMessageContent.getText().toString();

        if (!messageContent.isEmpty()) {
            AuthUser currentUser = Amplify.Auth.getCurrentUser();
            Message message = Message.builder().user(
                    User.justId(currentUser.getUserId())
            ).date(
                    new Temporal.DateTime(new Date(), getOffset())
            ).content(
                    messageContent
            ).build();
            Amplify.DataStore.save(message, target -> {
                Log.i("Amplify Datastore", "Message sent");
            }, onError -> {
                Log.e("Amplify Datastore", onError.getMessage());
            });
            txtMessageContent.setText("");
        }

    }

    private int getOffset() {
        GregorianCalendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        int rawOffset = timeZone.getRawOffset();
        return (int) TimeUnit.SECONDS.convert(rawOffset, TimeUnit.MILLISECONDS);
    }
}
