package utilities

import java.awt.{Dimension, Toolkit}
import java.util

import model.environment.CoordinateImpl

object Settings {

  object Constants {
    lazy val SCREEN_DIMENSION: Dimension = Toolkit.getDefaultToolkit.getScreenSize

    lazy val GAME_REFRESH_TIME = 100

    lazy val SCREEN_WIDTH: Int = SCREEN_DIMENSION.width

    lazy val SCREEN_HEIGHT: Int = SCREEN_DIMENSION.height

    lazy val FRAME_SIDE: Int = SCREEN_WIDTH / 3

    lazy val MAP_WIDTH = 50

    lazy val MAP_HEIGHT = 50

    lazy val TILE_HEIGHT = 1

    lazy val TILE_WIDTH = 1

    lazy val TILE_PIXEL = 32

    lazy val POKEMON_CENTER_HEIGHT = 5

    lazy val POKEMON_CENTER_WIDTH = 5

    lazy val POKEMON_CENTER_DOOR_X = 2

    lazy val POKEMON_CENTER_DOOR_Y = 4

    lazy val LABORATORY_HEIGHT = 4

    lazy val LABORATORY_WIDTH = 7

    lazy val LABORATORY_DOOR_X = 3

    lazy val LABORATORY_DOOR_Y = 3

    lazy val POKEMON_CENTER_INSIDE_WIDTH = 15

    lazy val POKEMON_CENTER_INSIDE_HEIGHT = 9

    lazy val LABORATORY_INSIDE_WIDTH = 13

    lazy val LABORATORY_INSIDE_HEIGHT = 13

    lazy val SECONDS_WAITING_TIME_FIGHT_REQUEST = 15

    lazy val DATABASE_FOLDER =  "/database/"

    lazy val INITIAL_TRAINER_LEVEL = 1

    lazy val LEVEL_STEP = 50

    lazy val BATTLE_EVENT_CHANGE_POKEMON = 1

    lazy val BATTLE_EVENT_CAPTURE_POKEMON = 1

    lazy val BATTLE_EVENT_ESCAPE = 2

    lazy val LOCAL_HOST_ADDRESS = "localhost"

    lazy val REMOTE_HOST_ADDRESS = "ec2-13-58-204-113.us-east-2.compute.amazonaws.com"

    lazy val REMOTE_HOST_PORT = 5672

    lazy val REMOTE_HOST_USERNAME = "guest"

    lazy val REMOTE_HOST_PASSWORD = "guest"

    lazy val REMOTE_DB_URL = "jdbc:mysql://ec2-13-58-204-113.us-east-2.compute.amazonaws.com:3306/poke_mp"

    lazy val REMOTE_DB_USER = "root"

    lazy val REMOTE_DB_PASSWORD = "ViroliRicci12"

    lazy val PLAYER_LOGIN_CHANNEL_QUEUE = "player_connection"

    lazy val PLAYERS_CONNECTED_CHANNEL_QUEUE = "players_connected"

    lazy val PLAYER_POSITION_CHANNEL_QUEUE = "player_position"

    lazy val PLAYER_LOGOUT_CHANNEL_QUEUE = "player_logout"

    lazy val TRAINER_DIALOGUE_CHANNEL_QUEUE = "trainer_dialogue"

    lazy val PLAYER_IN_BUILDING_CHANNEL_QUEUE = "player_in_building"

    lazy val PLAYER_IS_BUSY_CHANNEL_QUEUE = "player_is_busy"

    lazy val BATTLE_CHANNEL_QUEUE = "battle"

    lazy val PLAYER_POSITION_EXCHANGE = "player_position_exchange"

    lazy val NEW_PLAYER_EXCHANGE = "new_player_exchange"

    lazy val PLAYER_LOGOUT_EXCHANGE = "player_logout_exchange"

    lazy val PLAYER_IN_BUILDING_EXCHANGE = "player_in_building_exchange"

    lazy val PLAYER_IS_BUSY_EXCHANGE = "player_is_busy_exchange"

    lazy val INITIAL_PLAYER_POSITION = CoordinateImpl(25, 25)

    lazy val FONT_NAME = "Verdana"
  }

  object Audio {
    lazy val AUDIO_FOLDER = "/audio/"

    lazy val MAIN_SONG: String = AUDIO_FOLDER + "opening-theme.wav"

    lazy val MAP_SONG: String = AUDIO_FOLDER + "base-music.wav"

    lazy val POKEMONCENTER_SONG: String = AUDIO_FOLDER + "pokemoncenter.wav"

    lazy val LABORATORY_SONG: String = AUDIO_FOLDER + "laboratory.wav"

    lazy val HEALING_SOUND: String = AUDIO_FOLDER + "healing.wav"

    lazy val MENU_SONG: String = AUDIO_FOLDER + "menu.wav"

    lazy val POKEMON_WILD_SONG: String = AUDIO_FOLDER + "pokemon-wild.wav"

    lazy val CAPTURE_SONG: String = AUDIO_FOLDER + "pokemon-capture.wav"

    lazy val CAPTURE_FAILED_SONG: String = AUDIO_FOLDER + "pokemon-capture-failed.wav"
  }

  object Images {
    lazy val PANELS_FOLDER  =  "/panels/"

    lazy val IMAGES_FOLDER =  "/images/"

    lazy val MAP_IMAGES_FOLDER = "/images/maps/"

    lazy val CHARACTER_IMAGES_FOLDER = "/images/characters/"

    lazy val MAP_ELEMENTS_IMAGES_FOLDER: String = IMAGES_FOLDER + "mapElements/"

    lazy val POKEMON_IMAGES: String = IMAGES_FOLDER + "pokemon/"

    lazy val POKEBALL_IMAGES: String = IMAGES_FOLDER + "pokeball/"

    lazy val GAME_MENU_IMAGES: String = IMAGES_FOLDER + "gameMenu/"

    lazy val POKEMON_IMAGES_FRONT_FOLDER: String = POKEMON_IMAGES + "front/"

    lazy val POKEMON_IMAGES_BACK_FOLDER: String = POKEMON_IMAGES + "back/"

    lazy val POKEMON_IMAGES_ICON_FOLDER: String = POKEMON_IMAGES + "icon/"

    lazy val TRAINER_IMAGES_FOLDER: String = IMAGES_FOLDER + "trainer/"

    lazy val GRASS_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "grass.png"

    lazy val TALL_GRASS_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER+ "tall-grass.png"

    lazy val LABORATORY_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "laboratory.png"

    lazy val POKEMON_CENTER_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "pokemon-center.png"

    lazy val TREE_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "tree.png"

    lazy val WATER_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "water.png"

    lazy val WATER_MARGIN_TOP_LEFT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "water-margin-top-left.png"

    lazy val WATER_MARGIN_TOP_RIGHT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "water-margin-top-right.png"

    lazy val WATER_MARGIN_TOP_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "water-margin-top.png"

    lazy val WATER_MARGIN_BOTTOM_LEFT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "water-margin-bottom-left.png"

    lazy val WATER_MARGIN_BOTTOM_RIGHT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "water-margin-bottom-right.png"

    lazy val WATER_MARGIN_BOTTOM_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "water-margin-bottom.png"

    lazy val WATER_MARGIN_LEFT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "water-margin-left.png"

    lazy val WATER_MARGIN_RIGHT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "water-margin-right.png"

    lazy val ROAD_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "road.png"

    lazy val ROAD_MARGIN_TOP_LEFT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "road-margin-top-left.png"

    lazy val ROAD_MARGIN_TOP_RIGHT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "road-margin-top-right.png"

    lazy val ROAD_MARGIN_TOP_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "road-margin-top.png"

    lazy val ROAD_MARGIN_BOTTOM_LEFT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "road-margin-bottom-left.png"

    lazy val ROAD_MARGIN_BOTTOM_RIGHT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "road-margin-bottom-right.png"

    lazy val ROAD_MARGIN_BOTTOM_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "road-margin-bottom.png"

    lazy val ROAD_MARGIN_LEFT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "road-margin-left.png"

    lazy val ROAD_MARGIN_RIGHT_IMAGE_STRING: String = MAP_ELEMENTS_IMAGES_FOLDER + "road-margin-right.png"

    lazy val TRAINER_1_BACK_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1B1.png"

    lazy val TRAINER_1_BACK_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1B2.png"

    lazy val TRAINER_1_BACK_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1BS.png"

    lazy val TRAINER_1_FRONT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1F1.png"

    lazy val TRAINER_1_FRONT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1F2.png"

    lazy val TRAINER_1_FRONT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1FS.png"

    lazy val TRAINER_1_LEFT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1L1.png"

    lazy val TRAINER_1_LEFT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1L2.png"

    lazy val TRAINER_1_LEFT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1LS.png"

    lazy val TRAINER_1_RIGHT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1R1.png"

    lazy val TRAINER_1_RIGHT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1R2.png"

    lazy val TRAINER_1_RIGHT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "1RS.png"

    lazy val TRAINER_2_BACK_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2B1.png"

    lazy val TRAINER_2_BACK_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2B2.png"

    lazy val TRAINER_2_BACK_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2BS.png"

    lazy val TRAINER_2_FRONT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2F1.png"

    lazy val TRAINER_2_FRONT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2F2.png"

    lazy val TRAINER_2_FRONT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2FS.png"

    lazy val TRAINER_2_LEFT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2L1.png"

    lazy val TRAINER_2_LEFT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2L2.png"

    lazy val TRAINER_2_LEFT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2LS.png"

    lazy val TRAINER_2_RIGHT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2R1.png"

    lazy val TRAINER_2_RIGHT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2R2.png"

    lazy val TRAINER_2_RIGHT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "2RS.png"

    lazy val TRAINER_3_BACK_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3B1.png"

    lazy val TRAINER_3_BACK_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3B2.png"

    lazy val TRAINER_3_BACK_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3BS.png"

    lazy val TRAINER_3_FRONT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3F1.png"

    lazy val TRAINER_3_FRONT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3F2.png"

    lazy val TRAINER_3_FRONT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3FS.png"

    lazy val TRAINER_3_LEFT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3L1.png"

    lazy val TRAINER_3_LEFT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3L2.png"

    lazy val TRAINER_3_LEFT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3LS.png"

    lazy val TRAINER_3_RIGHT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3R1.png"

    lazy val TRAINER_3_RIGHT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3R2.png"

    lazy val TRAINER_3_RIGHT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "3RS.png"

    lazy val TRAINER_4_BACK_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4B1.png"

    lazy val TRAINER_4_BACK_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4B2.png"

    lazy val TRAINER_4_BACK_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4BS.png"

    lazy val TRAINER_4_FRONT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4F1.png"

    lazy val TRAINER_4_FRONT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4F2.png"

    lazy val TRAINER_4_FRONT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4FS.png"

    lazy val TRAINER_4_LEFT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4L1.png"

    lazy val TRAINER_4_LEFT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4L2.png"

    lazy val TRAINER_4_LEFT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4LS.png"

    lazy val TRAINER_4_RIGHT_1_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4R1.png"

    lazy val TRAINER_4_RIGHT_2_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4R2.png"

    lazy val TRAINER_4_RIGHT_S_IMAGE_STRING: String = TRAINER_IMAGES_FOLDER + "4RS.png"

    lazy val INFO_BUTTON: String = IMAGES_FOLDER + "info.png"

    lazy val POKEBALL_ICON: String = POKEBALL_IMAGES + "pokeballIcon.png"

    lazy val POKEBALL_IMAGE: String = POKEBALL_IMAGES + "pokeball.png"

    lazy val POKEBALL_OPEN_IMAGE: String = POKEBALL_IMAGES + "pokeballOpen.png"

    lazy val POKEBALL_RED_IMAGE: String = POKEBALL_IMAGES + "pokeballRed.png"

    lazy val UNKWOWN_POKEMON_IMAGE: String = POKEMON_IMAGES_ICON_FOLDER + "0.png"

    lazy val TRAINER_PANEL_BACKGROUND: String = PANELS_FOLDER + "trainer.jpg"

    lazy val TEAM_PANEL_BACKGROUND: String = PANELS_FOLDER + "pokemon-choice.png"

    lazy val SIGNIN_PANEL_BACKGROUND: String = PANELS_FOLDER + "sign-in.png"

    lazy val POKEMON_PANEL_BACKGROUND: String = PANELS_FOLDER + "pikachu.jpg"

    lazy val POKEDEX_PANEL_BACKGROUND: String = PANELS_FOLDER + "pokedex.png"

    lazy val LOGIN_PANEL_BACKGROUND: String = PANELS_FOLDER + "log-in.png"

    lazy val KEYBOARD_PANEL_BACKGROUND: String = PANELS_FOLDER + "keyboard.png"

    lazy val BOX_PANEL_BACKGROUND: String = PANELS_FOLDER + "box-pokemon.png"

    lazy val BATTLE_PANEL_BACKGROUND: String = PANELS_FOLDER + "battle.png"

    lazy val GAME_MENU_POKEDEX_ICON: String = GAME_MENU_IMAGES + "pokedex.png"

    lazy val GAME_MENU_TEAM_ICON: String = GAME_MENU_IMAGES + "team.png"

    lazy val GAME_MENU_TRAINER_ICON: String = GAME_MENU_IMAGES + "trainer.png"

    lazy val GAME_MENU_RANKING_ICON: String = GAME_MENU_IMAGES + "ranking.png"

    lazy val GAME_MENU_KEYBOARD_ICON: String = GAME_MENU_IMAGES + "keyboard.png"

    lazy val GAME_MENU_LOGOUT_ICON: String = GAME_MENU_IMAGES + "logout.png"

    lazy val GAME_MENU_EXIT_ICON: String = GAME_MENU_IMAGES + "exit.png"

  }

  object Strings {
    lazy val NEXT_DIALOGUE_BUTTON: String = "next"

    lazy val FINAL_DIALOGUE_BUTTON: String = "bye"

    lazy val DOCTOR_DIALOGUE_BUTTON: util.List[String] = util.Arrays.asList("yes", "no")

    lazy val TRAINER_DIALOGUE_BUTTON: util.List[String] = util.Arrays.asList("fight", FINAL_DIALOGUE_BUTTON)

    lazy val WAITING_TRAINER_LABEL: String = "Waiting for an answer from "

    lazy val LOGIN_BUTTON =  "Login"

    lazy val SIGN_IN_BUTTON =  "Sign in"

    lazy val QUIT_BUTTON =  "Quit"

    lazy val SUBMIT_BUTTON =  "Submit"

    lazy val USERNAME =  "Username"

    lazy val PASSWORD =  "Password"

    lazy val LOGIN_ERROR_USERNAME_PASSWORD_EMPTY = "Username and/or password must not be empty"

    lazy val LOGIN_ERROR_WRONG_USERNAME_PASSWORD = "Wrong username or password"

    lazy val LOGIN_NO_TRAINER_ERROR = "There is no trainer for this user"

    lazy val SIGN_IN_NAME_ERROR = "Name must be at least 3 characters"

    lazy val SIGN_IN_SURNAME_ERROR = "Surname must be at least 3 characters"

    lazy val SIGN_IN_EMAIL_ERROR = "Wrong e-mail"

    lazy val SIGN_IN_USERNAME_ERROR = "Username must be at least 4 characters"

    lazy val SIGN_IN_PASSWORD_ERROR = "Password must be at least 7 characters"

    lazy val CORRECT_SIGN_IN = "Successful sign-in"

    lazy val SIGN_IN_FAILED = "Username not available"

    lazy val WANT_TO_FIGHT = " wants to fight!"

    lazy val DONT_WANT_TO_FIGHT = " refused to fight :("

    lazy val TEAM_PANEL_INFO = "Use arrow keys to select your Pokemon, then SPACE to choose it."

    lazy val BUSY_MESSAGE = " is busy, try again later!"

    lazy val JOPTIONPANE_LOGOUT_TITLE = "Logout"

    lazy val JOPTIONPANE_LOGOUT_MESSAGE = "Do you really want to log out?"
  }
}