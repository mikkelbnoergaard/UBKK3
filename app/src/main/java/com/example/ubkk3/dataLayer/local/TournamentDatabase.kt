import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ubkk3.dataLayer.local.TournamentDao
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament

@Database(
    entities = [Tournament::class, MatchDetails::class, TeamDetails::class, Player:: class],
    version = 1,
    exportSchema = false
)
abstract class TournamentDatabase: RoomDatabase() {

    abstract val tournamentDao: TournamentDao

}
