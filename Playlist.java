
/**
 *  This class represents a Playlist object which can pass nothing for its initialization
 *  @author Christopher Rosales
 *  @ID #114328928
 *  @Assginment #1, Playlist and Song Record
 */
import java.util.Arrays;

//Important notes
// - I made a clear() method that sorts the arryay in a manner where there are no null between the SongRecord objects, this is so we don't have to loop through capacity each time but instead we can just loop through the size() (which is how many objects are in the array)
// - I also made an automatic formatter for scalability
public class Playlist {
  private static final int CAPACITY = 50; // size of SongRecord array
  private SongRecord[] list;
  private int manyItems = 0; // O(1) int for the size
  private String playlistName; // name playlists so we can fetch the object easier in the main
  private boolean flag = false;
  // now these two arrays are if we want to remove a song but that song either has
  // a max length for title, a max length for artist, or both, to prevent a nested
  // for loop from occuring to find the new max, we use arrays.sort (which is in
  // o(nlog(n))) average case.
  private int[] totalTitleLengths = new int[CAPACITY];
  private int[] totalArtistLengths = new int[CAPACITY];
  private int counter = 0;// for the arrays^^

  protected static int maxTitleLength = 10; // 10 is the default length for the format size
  protected static int maxArtistLength = 10; // 10 is also the default length for the format size
  private int artistSize;
  private int titleSize;
  private String formattedResult = "%-8s %-" + Integer.toString(maxTitleLength) + "s %-"
      + Integer.toString(maxArtistLength) + "s %-5s %n";

  /**
   * This constructor initializes the Playlist object and manyItems to 0
   * 
   * @param none
   */
  public Playlist() {
    // to add a song from an empty constructor you can do
    // Playlist p = new Playlist();
    // p.addSong(new SongRecord("title" , "artist" , 5, 15), 0);
    list = new SongRecord[CAPACITY];
  }

  /**
   * Description: Clone method returns a deep copy of original Playlist
   *
   * @return deep copy (clone) of song record
   *
   */
  public Object clone() { // returning a deep copy
    Playlist copy = new Playlist();
    int y = 0;
    for (SongRecord i : list) {
      if (i != null) {
        SongRecord newSong = new SongRecord(i.getSongTitle(), i.getSongArtist(), i.getMinutesLong(),
            i.getSecondsLong());
        copy.addSong(newSong, y);
      }
      y++;
    }
    return copy; // why do we need to type cast?
  }

  /**
   * Description: Equals method returns true if contents of both Playlists are
   * equal
   *
   * @param Object The obj (assuming it's an instance of the Playlist class) is
   *               being compared to the Playlist object
   * @return true if the Contents of both Playlist are equal in every position
   *         false if the contents of both Playlists are not equal in every
   *         position
   */
  public boolean equals(Object obj) {
    if (obj instanceof Playlist) {
      Playlist p = (Playlist) obj;
      if (this.size() == p.size()) {
        for (int i = 0; i < this.size(); i++) {
          SongRecord song1 = this.getSong(i);
          SongRecord song2 = p.getSong(i);
          if (!(song1.equals(song2))) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
    System.out.println("Object is not an instance of Playlist");
    return false;
  }

  /**
   * Description: Achieves the capacity of the SongRecord list (Playlist elements)
   *
   * @return manyItems (all the created objects in the list that are not null)
   *
   */

  public int size() { // O(1) complexity
    return manyItems;
  }

  /**
   * Description: Adding a song to the SongRecord list with scaling the format
   * when printing all the Songs in each Playlist if needed
   *
   * @param song     The SongRecord desired to be added to the selected playlist
   * @param position The position desired where the song wants to be placed (if
   *                 the position of a song is already there then we insert the
   *                 song at that position and move all the other elements one
   *                 forward)
   *
   * @exception IllegalArgumentException to make sure that the index is in range
   *                                     of 0-49
   * @throws FullPlaylistException to make sure that the range for position is
   *                               0-49
   *
   */
  public void addSong(SongRecord song, int position) {
    // if there is already an element in for position , insert it
    try {
      if (position >= 50 || position < 0)
        throw new IllegalArgumentException();
      if (this.size() + 1 >= CAPACITY) {
        flag = true;
        throw new FullPlaylistException("EXCEEDING CAPACITY");
      }
      if (list[position] != null && this.size() + 1 < CAPACITY) { // creating an open space to insert song if one
                                                                  // already
                                                                  // exists in its position
        SongRecord[] copy = list.clone(); // we have create a clone, otherwise it'll throw an exception
        for (int i = position; i < CAPACITY - 1; i++) {
          list[i + 1] = copy[i];
        }
      }
      list[position] = song;
      manyItems++;
      // these two if statements are checking if we need to increase the spaceing when
      // printing the songs
      if (list[position].getSongTitle().length() > maxTitleLength) {
        maxTitleLength = list[position].getSongTitle().length();
        totalTitleLengths[counter] = list[position].getSongTitle().length();
        titleSize++;
      }
      if (list[position].getSongArtist().length() > maxArtistLength) {
        maxArtistLength = list[position].getSongArtist().length();
        totalArtistLengths[counter] = list[position].getSongArtist().length();
        artistSize++;
      }
    } catch (IllegalArgumentException | FullPlaylistException e) { // I don't understand why I coudn't throw an
                                                                   // IndexOutOfBoundsException, it could've been less
                                                                   // lines
      if (flag)
        System.out.println("This Playlist is full!");
      else
        System.out.println("The size of the Playlist is between 1-50");
      PlaylistOperations.main(null);
    }
    this.clear(); // we clear it so there's no null's between any of the SongRecord objects in the
                  // list
  }

  /**
   * Description: Removing a song to the SongRecord list with scaling the format
   * when printing all the Songs in each Playlist if needed
   *
   * @param position The position desired where the song will be removed from
   *
   * @exception IllegalArgumentException to make sure that the range for position
   *                                     is 0-49
   *
   */
  public void removeSong(int position) {
    try {
      if (position >= 50 || position < 0)
        throw new IllegalArgumentException();
      // these if and else statements check if the song we are removing from the
      // playlist was the max length for either the title and the artist (to create a
      // new format spacing), because of this we need to find the second longest size
      // of either (or both) one
      if (list[position].getSongTitle().length() == maxTitleLength && titleSize > 1) {
        totalTitleLengths[totalTitleLengths.length - 1] = 0;
        Arrays.sort(totalTitleLengths);
        maxTitleLength = totalTitleLengths[totalTitleLengths.length - 1];
        titleSize--;
        // then we get the new max length title
      } else if (list[position].getSongTitle().length() == maxTitleLength && titleSize == 1) {
        maxTitleLength = 10; // reset to default
        totalTitleLengths[totalTitleLengths.length - 1] = 0;
      }
      if (list[position].getSongArtist().length() == maxArtistLength && artistSize > 1) {
        totalArtistLengths[totalArtistLengths.length - 1] = 0;
        Arrays.sort(totalArtistLengths);
        maxArtistLength = totalArtistLengths[totalArtistLengths.length - 1];
        artistSize--;
        // then we get the new max length for the artist
      } else if (list[position].getSongArtist().length() == maxArtistLength && artistSize == 1) {
        maxArtistLength = 10; // reset back to default
        totalArtistLengths[totalArtistLengths.length - 1] = 0;
      }
      list[position] = null;
      manyItems--;
      this.clear();
    } catch (IllegalArgumentException e) {
      System.out.println("The size of the Playlist is between 1-50");
      PlaylistOperations.main(null);
    }
  }

  /**
   * Description: Mutates the playlist name from an inputted new name
   *
   * @param newName The new name set for the playlist name
   *
   */
  public void setPlaylistName(String newName) {
    playlistName = newName;
  }

  /**
   * Description: Retrieves the playlist name
   *
   * @return Returns the name of the playlist
   *
   */
  public String getPlaylistName() {
    return playlistName;
  }

  /**
   * Description: Retrieves the song object from the playlist class
   *
   * @param position The position desired to retrieve the song
   *
   * @return list[position], the song object from the SongRecord list in the
   *         Playlist class
   *
   * @exception IllegalArgumentException to check if the index is in the range of
   *                                     1-49
   * @exception NullPointerException     to check if there's a null value in
   *                                     list[position]
   *
   */
  public SongRecord getSong(int position) {
    try {
      if (position >= 50 || position < 0)
        throw new IllegalArgumentException();
      if (list[position] == null) {
        flag = true;
        throw new NullPointerException();
      }
      SongRecord answer = list[position]; // not completely sure if I need this line..
    } catch (IllegalArgumentException | NullPointerException e) {
      if (flag)
        System.out.println("There must be a song in the position you select");
      else
        System.out.println("The size of the Playlist is between 1-50");
      PlaylistOperations.main(null);
    }
    return list[position];
  }

  /**
   * Description: Searches the song and prints the song from the given position
   *
   * @param position The position is needed to retrieve and print the song desired
   *
   *
   * @exception IllegalArgumentException to make sure that the position is in
   *                                     range from 1-49
   *
   */
  public void searchSong(int position) { // I wanted to implement a method called search song just so It could print the
                                         // song right after I call the method along with inputting the position I would
                                         // like to find
    try {
      if (position >= 50 || position < 0)
        throw new IllegalArgumentException();
      Playlist temp = new Playlist();
      temp.addSong(list[position], 0);
      temp.printAllSongs();
    } catch (IllegalArgumentException e) {
      System.out.println("The size of the Playlist is between 1-50");
    }
  }

  /**
   * Description: Prints all the songs in the Playlist object
   *
   */
  public void printAllSongs() {
    System.out.println(this.toString());
  }

  /**
   * Description: Retrieves all the songs with the inputted artist
   *
   * @param originalList The list that is being search for an artist
   * @param artist       The artist for the songs to be found
   *
   * @return Returns a new list that contains the only songs from the inputted
   *         artist
   *
   */
  public static Playlist getSongsByArtist(Playlist originalList, String artist) {
    Playlist newList = new Playlist();
    int y = 0;
    for (int i = 0; i < originalList.size(); i++) {
      if (originalList.getSong(i).getSongArtist().equals(artist)) {
        newList.addSong(originalList.getSong(i), y);
        y++;
      }
    }
    return newList;
  }

  /**
   * Description: Clearing the null's that are in between the Song Records in the
   * array so we can loop through the size() (manyItems) instead of the CAPACITY
   *
   */
  private void clear() {
    SongRecord[] temp = new SongRecord[CAPACITY];
    int y = 0;
    for (int i = 0; i < list.length; i++) {
      if (list[i] != null) {
        temp[y] = this.getSong(i);
        y++;
      }
    }
    list = temp.clone(); // might need to type cast this
  }

  /**
   * Description: Prints the toString method of the Playlist object class
   *
   * @return All the song's in the Playlist class
   *
   */
  public String toString() {
    String result = String.format(this.updateFormatting(maxTitleLength, maxArtistLength), "Song", "Title", "Artist",
        "Length");
    result += "-".repeat(17 + maxTitleLength + maxArtistLength) + "\n";
    for (int i = 0; i < this.size(); i++) {
      result += String.format("%-9s", i + 1);
      result += list[i].toStringIntegrated();
    }
    return result;
  }

  /**
   * Description: checking if the Playlist class is empty
   *
   * @return True if the size is == 0 False if the size is != 0
   *
   */
  public boolean isEmpty() {
    return (this.size() == 0);
  }

  /**
   * Description: Updates the formatting size for printing all the songs, if
   * there's a new max or new min, it's crucial to update the formatting every
   * printAllSongs() iteration
   *
   * @param maxTitleLength  The greatest title length in the Playlist class
   * @param maxArtistLength The greatest artist length in the Playlist class
   *
   * @return the formatted result for the toString() method
   *
   */
  public String updateFormatting(int maxTitleLength, int maxArtistLength) {
    formattedResult = "%-8s %-" + Integer.toString(maxTitleLength) + "s %-" + Integer.toString(maxArtistLength)
        + "s %-5s %n";
    return formattedResult;
  }

  /**
   * Description: Combines two inputted Playlist classes with checking if both
   * combined playlist sizes will be <= 50
   *
   * @param p1 Playlist 1 to be combined with p2
   * @param p2 Playlist 2 to be combined with p1
   *
   * @return The union of the two inputted playlists with
   *
   */
  public static Playlist union(Playlist p1, Playlist p2) {
    if (p1.size() + p2.size() >= 50) {
      System.out.println("Cannot merge playlists because it will surpass the capacity of 50!");
      PlaylistOperations.main(null);
    }
    Playlist answer = new Playlist();
    System.arraycopy(p1.list, 0, answer.list, 0, p1.manyItems);
    System.arraycopy(p2.list, 0, answer.list, p1.manyItems, p2.manyItems);
    answer.manyItems = p1.manyItems + p2.manyItems;
    return answer;
  }
}
