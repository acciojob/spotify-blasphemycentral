package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap; //list of songs in a playlist
    public HashMap<Playlist, List<User>> playlistListenerMap; //list of users listening to a playlist
    public HashMap<User, Playlist> creatorPlaylistMap; //which user created which playlist
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile)
    {
        for(User curUser: users)
        {
            if(curUser.getMobile().equals(mobile)){
                return curUser;
            }
        }
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name)
    {
        for(Artist artist: artists)
        {
            if(artist.getName().equals(name))
                return artist;
        }
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName)
    {
        for(Album album : albums)
        {
            if(album.getTitle().equals(title))
                return  album;
        }
        for(Artist artist : artists)
        {
            if(artist.getName().equals(artistName))
            {
                List<Album> list = new ArrayList<>();
                if(artistAlbumMap.containsKey(artist)) list = artistAlbumMap.get(artist);
                Album album = new Album(title);
                albums.add(album);
                list.add(album);
                artistAlbumMap.put(artist, list);
                return album;
            }
        }
        Artist artist = createArtist(artistName);
        List<Album> list = new ArrayList<>();
        Album album = new Album(title);
        albums.add(album);
        list.add(album);
        artistAlbumMap.put(artist, list);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception
    {
        for(Album album : albums)
        {
            if(album.getTitle().equals(albumName))
            {
                List<Song> list = new ArrayList<>();
                if(albumSongMap.containsKey(album)) list = albumSongMap.get(album);
                Song song = new Song(title, length);
                list.add(song);
                songs.add(song);
                albumSongMap.put(album, list);
                return song;
            }
        }
        throw new Exception("Album does not exist");
//        Album album = new Album(albumName);
//        albums.add(album);
//        List<Song> list = new ArrayList<>();
//        Song song = new Song(title, length);
//        list.add(song);
//        songs.add(song);
//        albumSongMap.put(album, list);
//        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception
    {
        for(Playlist playlist : playlists)
        {
            if(playlist.getTitle().equals(title))
                return  playlist;
        }
        List<Song> list = new ArrayList<>();
        for(Song song : songs)
        {
            if(song.getLength() == length) list.add(song);
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        playlistSongMap.put(playlist, list);
        for(User user : users)
        {
            if(user.getMobile().equals(mobile))
            {
                List<User> usersList = new ArrayList<>();
                if(playlistListenerMap.containsKey(playlist)) usersList = playlistListenerMap.get(playlist);
                usersList.add(user);
                playlistListenerMap.put(playlist,usersList);

                creatorPlaylistMap.put(user, playlist);

                List<Playlist> userPlaylists = new ArrayList<>();
                if(userPlaylistMap.containsKey(user))
                    userPlaylists = userPlaylistMap.get(user);
                userPlaylists.add(playlist);
                userPlaylistMap.put(user, userPlaylists);

                return playlist;
            }
        }
        throw new Exception("User does not exist");
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception
    {
        for(Playlist playlist : playlists)
        {
            if(playlist.getTitle().equals(title))
                return  playlist;
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        List<Song> list = new ArrayList<>();
        for(Song song : songs)
        {
            if(songTitles.contains(song.getTitle()))
                list.add(song);
        }
        playlistSongMap.put(playlist, list);

        for(User user : users)
        {
            if(user.getMobile().equals(mobile))
            {
                List<User> usersList = new ArrayList<>();
                if(playlistListenerMap.containsKey(playlist)) usersList = playlistListenerMap.get(playlist);
                usersList.add(user);
                playlistListenerMap.put(playlist,usersList);

                creatorPlaylistMap.put(user, playlist);

                List<Playlist> userPlaylists = new ArrayList<>();
                if(userPlaylistMap.containsKey(user))
                    userPlaylists = userPlaylistMap.get(user);
                userPlaylists.add(playlist);
                userPlaylistMap.put(user, userPlaylists);

                return playlist;
            }
        }
        throw new Exception("User does not exist");
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception
    {
        for (Playlist playlist : playlists)
        {
            if(playlist.getTitle().equals(playlistTitle))
            {
                for(User user : users)
                {
                    if(user.getMobile().equals(mobile))
                    {
                        List<User> usersList = new ArrayList<>();
                        if(playlistListenerMap.containsKey(playlist))
                            usersList = playlistListenerMap.get(playlist);
                        if(!usersList.contains(user))usersList.add(user);
                        playlistListenerMap.put(playlist,usersList);

                        if(creatorPlaylistMap.get(user)!=playlist)
                            creatorPlaylistMap.put(user,playlist);

                        List<Playlist>userplaylists = new ArrayList<>();
                        if(userPlaylistMap.containsKey(user))
                        {
                            userplaylists=userPlaylistMap.get(user);
                        }
                        if(!userplaylists.contains(playlist))userplaylists.add(playlist);
                        userPlaylistMap.put(user,userplaylists);


                        return playlist;
                    }
                }
                throw new Exception("User does not exist");
            }
        }
        throw new Exception("Playlist does not exist");
    }

    public Song likeSong(String mobile, String songTitle) throws Exception
    {
        for(User user : users)
        {
            if (user.getMobile().equals(mobile))
            {
                for(Song song : songs)
                {
                    if(song.getTitle().equals(songTitle))
                    {
                        List<User> users = new ArrayList<>();
                        if(songLikeMap.containsKey(song)){
                            users=songLikeMap.get(song);
                        }
                        if (!users.contains(user))
                        {
                            users.add(user);
                            songLikeMap.put(song, users);
                            song.setLikes(song.getLikes() + 1);

                            Album album = new Album();
                            for(Album curAlbum : albumSongMap.keySet())
                            {
                                List<Song> temp = albumSongMap.get(curAlbum);
                                if(temp.contains(song))
                                {
                                    album=curAlbum;
                                    break;
                                }
                            }

                            Artist artist = new Artist();
                            for(Artist curArtist : artistAlbumMap.keySet())
                            {
                                List<Album> temp = artistAlbumMap.get(curArtist);
                                if(temp.contains(album))
                                {
                                    artist=curArtist;
                                    break;
                                }
                            }

                            artist.setLikes(artist.getLikes()+1);
                        }
                        return song;
                    }
                }
                throw new Exception("Song does not exist");
            }
        }
        throw new Exception("User does not exist");
    }

    public String mostPopularArtist()
    {
        String name="";
        int maxLikes = Integer.MIN_VALUE;
        for(Artist artist : artists)
        {
            maxLikes= Math.max(maxLikes,artist.getLikes());
        }
        for(Artist artist : artists)
        {
            if(maxLikes==artist.getLikes())
            {
                name=artist.getName();
            }
        }
        return name;
    }

    public String mostPopularSong()
    {
        String name="";
        int maxLikes = Integer.MIN_VALUE;
        for(Song song : songs)
        {
            maxLikes=Math.max(maxLikes,song.getLikes());
        }
        for(Song song : songs)
        {
            if(maxLikes==song.getLikes())
                name=song.getTitle();
        }
        return name;
    }
}
