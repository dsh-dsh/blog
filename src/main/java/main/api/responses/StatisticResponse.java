package main.api.responses;

import lombok.Data;

@Data
public class StatisticResponse {

    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private long firstPublication;

}
