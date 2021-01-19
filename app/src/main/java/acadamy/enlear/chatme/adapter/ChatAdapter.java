package acadamy.enlear.chatme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Message;

import java.util.List;

import acadamy.enlear.chatme.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChatAdapter extends ArrayAdapter<Message> {

    public ChatAdapter(@NonNull Context context, int resource, @NonNull List<Message> messages) {
        super(context, resource, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message message = getItem(position);

        boolean isCurrentUserMessage = message.getUser().getId().equals(Amplify.Auth.getCurrentUser().getUserId());
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if(isCurrentUserMessage){
            view = layoutInflater.inflate(R.layout.message_sent_layout, parent, false);
        }else{
            view = layoutInflater.inflate(R.layout.message_received_layout, parent, false);
            TextView nameText = view.findViewById(R.id.name);
            nameText.setText(message.getUser().getName());
        }
        TextView messageContentText = view.findViewById(R.id.messageContent);
        TextView messageDateText = view.findViewById(R.id.messageDate);

        messageContentText.setText(
                message.getContent()
        );

        messageDateText.setText(
                message.getDate().format()
        );
        return view;
    }
}
