
package org.example.lab6javafx.Service;

import org.example.lab6javafx.domain.Friendship;
import org.example.lab6javafx.domain.Utilizator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SocialCommunities {
    /**
     * creates social communities
     * @param socialNetwork
     *
    public SocialCommunities(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    void DFS(Long v, HashMap<Long, Boolean> visited){
        visited.put(v, true);
        System.out.println(v + " " + this.socialNetwork.findUser(v).getFirstName() + " " + this.socialNetwork.findUser(v).getLastName());
        if(adjList.containsKey(v)){
            for(Long x: adjList.get(v)){
                if(!visited.containsKey(x)){
                    DFS(x,visited);
                }
            }
        }
    }

    /**
     * counts the number of connected communities
     * @return the number of connected communities
     *
    public int connectedCommunities(){
        adjList = new HashMap<Long, List<Long>>();
        for(Utilizator user: socialNetwork.getUsers()) {
            List<Long> friends = new ArrayList<>();
            for (Friendship friendship : socialNetwork.getFriendships()) {
                if (friendship.getIdUser1().equals(user.getId()))
                    friends.add(friendship.getIdUser2());
                if (friendship.getIdUser2().equals(user.getId()))
                    friends.add(friendship.getIdUser1());
            }
            if (!friends.isEmpty()) {
                this.adjList.put(user.getId(), friends);
            }
        }
        List<Long> ids = new ArrayList<>();
        for(Utilizator user: socialNetwork.getUsers())
            ids.add(user.getId());

        int nrOfCommunities = 0;
        HashMap<Long, Boolean> visited = new HashMap<Long, Boolean>();
        for(Long v : ids){
            if(!visited.containsKey(v)){
                DFS(v,visited);
                nrOfCommunities++;
                System.out.println();
            }
        }
        return nrOfCommunities;
    }

    /**
     * determines the most social community
     * @return the most social community
     *
    public List<Long> mostSocialCommunity(){
        adjList = new HashMap<Long, List<Long>>();
        List<Long> max = new ArrayList<>();
        for(Utilizator user: socialNetwork.getUsers()){
            List<Long> friends = new ArrayList<>();
            for (Friendship friendship : socialNetwork.getFriendships()) {
                if(friendship.getIdUser1().equals(user.getId()))
                    friends.add(friendship.getIdUser2());
                if(friendship.getIdUser2().equals(user.getId()))
                    friends.add(friendship.getIdUser1());
            }
            if (!friends.isEmpty()) {
                this.adjList.put(user.getId(), friends);
                if(max.size() < friends.size()+1){
                    max=friends;
                    max.add(user.getId());
                }
            }
        }
        return max;
    }*/
}
