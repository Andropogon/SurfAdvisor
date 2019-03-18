package livewind.example.andro.liveWind.Comments;

import java.util.Comparator;


class CommentComparator implements Comparator<Comment> {
    public int compare(Comment comment1, Comment comment2) {
        if(comment1.getCommentNumber().equals("0")){
            return 1;
        }
        if(comment2.getCommentNumber().equals("0")){
            return -1;
        }

        long long1 = comment1.getTimestamp();
        int timestamp1 = (int) long1;
        long long2 = comment2.getTimestamp();
        int timestamp2 = (int) long2;
        //If it is new comment
        if(timestamp1==0){
            return -1;
        }
        if(timestamp2==0){
            return 1;
        }

        return timestamp2 - timestamp1;
    }
}