package com.example.chesstheoryalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import no.bakkenbaeck.chessboardeditor.view.board.ChessBoardView
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random.Default.nextInt


/*
* TODO:
*  settings (add/remove force shutoff alarm button)
*  show solution
*  sound! that disables for 20 sek on touch, for 10 mins
* */

// alarm service
// https://www.geeksforgeeks.org/how-to-build-a-simple-alarm-setter-app-in-android/

// gui chess
// https://github.com/bakkenbaeck/chessboardeditor?fbclid=IwAR25dxzSqlwTnrvYYktfkIgFTwIaRAuxSrkWm8eaeYTf7Z90lNl1Aa6gJEo

// fen to chessboard
// https://github.com/bhlangonijr/chesslib#Get_FEN_string_from_chessboard



class Board(private val FEN: String){

    var board = Array<Char>(64){'X'}

    // small letters are black, big letters are white
    init {
        var counter = 0
        for(letter in FEN) {
            if(letter.isDigit()){
                counter += Character.getNumericValue(letter)
            }else if(letter != '/'){
                if(counter<64){
                    board[counter] = letter
                }
                counter ++
            }
        }
    }

    fun printChessboard(){ // debugging
        Log.e("debug", "PRINT ME")
        var appender: String = " "
        for (i in 0..63){
            appender = appender.plus(board[i])
        }
        Log.e("debug", appender)
    }

    fun hasSquareBlackPiece(from: String): Boolean {

        val coordinates = mapOf('A' to 0, 'B' to 1, 'C' to 2, 'D' to 3, 'E' to 4, 'F' to 5, 'G' to 6, 'H' to 7)

        val from_letter: Char = from[0]
        val from_num: Char = from[1]
        val startCoo = (8-Character.getNumericValue(from_num)) * 8 + (coordinates[from_letter]!!)
        return when(board[startCoo]){
            'r', 'n', 'b', 'q', 'k', 'p' -> true // match either b or c
            else -> false
        }
    }

    fun doMove(from: String, to: String){

        val coordinates = mapOf('A' to 0, 'B' to 1, 'C' to 2, 'D' to 3, 'E' to 4, 'F' to 5, 'G' to 6, 'H' to 7)

        val from_letter: Char = from[0]
        val from_num: Char = from[1]

        val to_letter: Char = to[0]
        val to_num: Char = to[1]

        val startCoo = (8-Character.getNumericValue(from_num)) * 8 + (coordinates[from_letter]!!)

        val pieceToMove = board[startCoo!!]
        board[startCoo] = 'X'

        val endCoo =  (8-Character.getNumericValue(to_num)) * 8 + (coordinates[to_letter]!!)
        board[endCoo!!] = pieceToMove

        Log.e("debug", "we want to move form start to stop:")
        Log.e("debug", startCoo.toString())
        Log.e("debug", endCoo.toString())

    }

    fun getFen(): String {

        var FENbuilder = ""
        var digitCounter = 0
        for(i in 0..63){

            // on the first index we add /
            if (i % 8 == 0){
                if(digitCounter > 0){
                    FENbuilder = FENbuilder.plus(digitCounter.toString())
                    digitCounter = 0
                }
                if(i != 0){
                    FENbuilder = FENbuilder.plus('/')
                }
            }

            // add whtas in the square
            if (board[i] == 'X'){
                digitCounter ++
            }else{
                if (digitCounter > 0){
                    FENbuilder = FENbuilder.plus(digitCounter.toString())
                    digitCounter = 0
                }
                FENbuilder = FENbuilder.plus(board[i])
            }

            if(i==63 && digitCounter>0){
                FENbuilder = FENbuilder.plus(digitCounter.toString())
                digitCounter = 0
            }
        }

        return FENbuilder
    }
}

class ChessAvtivity : AppCompatActivity() {

    private var currentProblemIndex = 2
    private var wereAtMove = 0
    private var currentFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"
    private var currentSolution = emptyList<String>()


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess_avtivity)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.title = "Chessboard"

        val sp : SharedPreferences = this.getSharedPreferences("name",AppCompatActivity.MODE_PRIVATE)
        sp.edit().putBoolean("changeActivity2", false).apply();
        Log.e("cal", "changed back to starting with main activity")

        val i = Intent(this, OnAlarmReceiver::class.java)
        Log.e("rec", "clicked")
        val pendingIntent = PendingIntent.getBroadcast(this, 0, i,
                PendingIntent.FLAG_ONE_SHOT)

        val aManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(baseContext, this::class.java)
        val pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)



        // if we have saved app state data, override currentProblemIndex
        this.currentProblemIndex = 11
        // etc.

        val inputStream: InputStream = this.assets.open("puzzles.csv")

        val reader = inputStream.bufferedReader()

        var fenList: ArrayList<String> = ArrayList()

        // read all FENs from dataset
        for (line in reader.lines()) {
            fenList.add(line)
        }

        /*
        board debug

        var board = ChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR")
        board.printChessboard()
        Log.e("debug", board.getFen())
        board.doMove("E2", "E4")
        board.printChessboard()
        Log.e("debug", board.getFen())
        */


        setNextFENandSolution(fenList)


         //"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1" //

        val chessBoard: ChessBoardView = findViewById<ChessBoardView>(R.id.chessBoard)

        //chessBoard.setFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")


        chessBoard.setFen(this.currentFEN)


        val delay = 500 // 1000 milliseconds == 1 second

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                handler.postDelayed(this, delay.toLong())

                var newFEN = chessBoard.getFen()

                // we check if a change has happened in the FEN on the phone
                // if a change happened, we compare the user's FEN with the FEN what would have resulted in the [i]th position of the correct line
                // if fail, it retraces one move
                // note that the computer makes every second move


                Log.e("debug", "waiting for update")

                if (currentFEN != newFEN) {
                    Log.e("debug", "Position Changed!!!!!!!!!!!!!!!")
                    Log.e("debug", "old pos")
                    Log.e("debug", currentFEN)
                    Log.e("debug", "new pos")
                    Log.e("debug", newFEN)

                    aManager.cancel(pIntent) // cancel the alarm

                    if (isCorrectMoveThenMakeComputerMove(newFEN)) {
                        Log.e("debug", "updating global pos")
                        currentFEN = newFEN
                        Log.e("debug", "it was correct")
                        wereAtMove++

                        if (wereAtMove >= currentSolution.size) {
                            // new prob
                            setNextFENandSolution(fenList)
                        } else {
                            setFenAfterComputerMove()
                        }


                    } else {
                        // go back to previous position
                        chessBoard.setFen(currentFEN)

                    }

                    // currentFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
                    // updateChessboardFreeze(chessBoard, currentFEN)
                }
            }
        }, delay.toLong())


    }



    private fun setFenAfterComputerMove() {

        val board = Board(currentFEN)

        Log.e("debug", "wereAtMove" + wereAtMove)

        var firstCoordinate = currentSolution[wereAtMove].substring(0, 2).toUpperCase()
        var secondCoordinate = currentSolution[wereAtMove].substring(2, 4).toUpperCase()


        Log.e("debug", "making computer move")
        Log.e("debug", firstCoordinate + " " + secondCoordinate)


        board.doMove(firstCoordinate, secondCoordinate)

        Log.e("debug", "the resulting board fen after the computer move is")
        Log.e("debug", board.getFen())

        this.currentFEN = board.getFen()
        this.wereAtMove ++

    }



    fun isCorrectMoveThenMakeComputerMove(newFEN: String): Boolean {

        // the fen that we have originally
        val board = Board(currentFEN)

        // the fen we would have gotten if we made the correct move
        var firstCoordinate = currentSolution[this.wereAtMove].substring(0, 2).toUpperCase()
        var secondCoordinate = currentSolution[this.wereAtMove].substring(2, 4).toUpperCase()

        board.doMove(firstCoordinate, secondCoordinate)
        val afterCorrectMoveFEN = board.getFen()


        // compare the correct FEN to the FEN that we got from the user
        // if correct move:
        Log.e("debug", "was correct answer?")
        Log.e("debug", (newFEN == afterCorrectMoveFEN).toString())
        return (newFEN == afterCorrectMoveFEN)

        /*
        if wholeproblemsolved{
            currentProblemIndex++
            setCurrentFENansSolution()
        }*/

    }



    private fun setNextFENandSolution(fenList: ArrayList<String>) {

        // fetch the fist index that is a black-moves-first problem


        this.wereAtMove = 0
        this.currentProblemIndex = (0..(fenList.size-1)).random()


        // next problem is a random problem


        var searchingForProblem = true
        while (searchingForProblem){

            if (currentProblemIndex < fenList.size){

                var fenInnerList = fenList[currentProblemIndex].split(",")
                var moves = fenInnerList[2].split(" ")
                var firstCoordinate = moves[0].substring(0, 2).toUpperCase()
                val board = Board(fenInnerList[1].split(" ")[0]) // get simplest form of FEN

                if (board.hasSquareBlackPiece(firstCoordinate)){
                    // SUCCESS

                    Log.e("debug", "found a black-moves-first problem at index ")
                    Log.e("debug", currentProblemIndex.toString())


                    this.currentFEN = fenInnerList[1].split(" ")[0]
                    this.currentSolution = moves



                    Log.e("debug", "setting up FEN")
                    Log.e("debug", currentFEN)
                    Log.e("debug", "with solution")
                    Log.e("debug", currentSolution.toString())

                    searchingForProblem = false
                    break

                } else {

                    Log.e("debug", "DID NOT find a black-moves-first problem at index ")
                    Log.e("debug", currentProblemIndex.toString())

                    this.currentProblemIndex = (0..(fenList.size-1)).random()
                    // try again
                }
            }
        }

        setFenAfterComputerMove()
    }

    fun updateChessboardFreeze(chessBoard: ChessBoardView, fen: String) {
        Thread.sleep(2000L)
        chessBoard.setFen(fen)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}