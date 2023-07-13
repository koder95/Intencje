<?php 
	/*
	Plugin Name: Intencje
	Plugin URI: https://github.com/koder95/Intencje
	Description: Wtyczka umożliwia prezentację, tworzenie i usuwanie intencji parafialnych.
	Version: 1.0.1
	Author: Kamil Mularski
	Author URI: https://github.com/koder95
	*/
	
	// Make sure we don't expose any info if called directly
	if ( !function_exists( 'add_action' ) ) {
		echo 'Siemka! Jestem tylko wtyczką i nie powinnam być uruchamiana bezpośrednio.';
		exit;
	}
	
	register_activation_hook( __FILE__, 'intencje_activation' );
	register_deactivation_hook( __FILE__, 'intencje_deactivation' );
	
	add_shortcode( 'intencje', 'intencje_shortcode' );
	
	function intencje_activation() {
		// TODO: utworzenie wymaganych tabel, jeżeli one nie istnieją
		
		global $wpdb, $intencje_table_name, $intencje_nazwy_table_name;
		$intencje_table_name = $wpdb->prefix . 'intencje';
		$intencje_nazwy_table_name = $intencje_table_name . '_nazwy';
		
		$charset_collate = $wpdb->get_charset_collate();

		if( $wpdb->get_var( "SHOW TABLES LIKE '$intencje_table_name'" ) != $intencje_table_name ) {
			
			$wpdb->query( "CREATE TABLE $intencje_table_name (
			  `msza` datetime NOT NULL,
			  `kaplica` tinytext,
			  `intencja` text NOT NULL,
			  PRIMARY KEY  (`msza`)
			) $charset_collate;" );
			
		}

		if( $wpdb->get_var( "SHOW TABLES LIKE '$intencje_nazwy_table_name'" ) != $intencje_nazwy_table_name ) {
			
			$wpdb->query( "CREATE TABLE $intencje_nazwy_table_name (
			  `data` date NOT NULL,
			  `nazwa` tinytext NOT NULL,
			  PRIMARY KEY  (`data`)
			) $charset_collate;" );
			
		}
		
		// Clear the permalinks after the post type has been registered.
		flush_rewrite_rules(); 
	}
	
	function intencje_deactivation() {
		// do nothing (but maybe in the future this could be changed)
		remove_shortcode( 'intencje' );
		
		// Clear the permalinks to remove our post type's rules from the database.
		flush_rewrite_rules();
	}
	
	function intencje_shortcode( $atts = [], $content = null ) {
		$today = date_create();
		$timezone = new DateTimeZone("Europe/Warsaw");
		$end = date_create("next sunday", $timezone);
		$begin = clone $end;
		$begin->modify("-1 week");
		return intencje_css() . intencje_to_table("Intencje", $begin, $end);
	}
	
	function intencje_css() {
		return "<style>
			.kaplica {
				font-size: 9pt;
			}
			table.intencje {
				border-collapse: collapse;
			}
			table.intencje td {
				border: 1px solid #000;
				min-width: 50px;
				padding: 5px;
			}
			.intencje-dzień {
				background-color: #008800;
				color: #fff;
			}
		</style>";
	}
	
	function intencje_to_table( $title, $date_begin, $date_end ) {
		$today = date_create("", new DateTimeZone("Europe/Warsaw"));
		$html = "<table class=\"intencje\" data-begin=\"{$date_begin->format("Y-m-d H:i")}\" data-end=\"{$date_end->format("Y-m-d H:i")}\" data-today=\"{$today->format("Y-m-d H:i")}\"><caption>$title</caption><tbody>";
		for ($date = $date_begin; $date <= $date_end; $date->modify('+1 day')) {
			$html .= intencje_day ($date);
		}
		return $html . "</tbody></table>";
	}
	
	function intencje_day( $date ) {
		global $wpdb, $intencje_table_name, $intencje_nazwy_table_name;
		$intencje_table_name = $wpdb->prefix . 'intencje';
		$intencje_nazwy_table_name = $intencje_table_name . '_nazwy';
		$name = null;
		
		$fmt = new IntlDateFormatter("pl_PL", IntlDateFormatter::FULL, IntlDateFormatter::SHORT, 'Europe/Warsaw', IntlDateFormatter::GREGORIAN);
		
		$fmt->setPattern("yyyy-MM-dd");
		$result = $wpdb->get_results( "SELECT * FROM `$intencje_nazwy_table_name` WHERE DATE(`data`) = DATE('{$date->format("Y-m-d")}');" );
		if (count($result) > 0) {
			$name = $result[0]->nazwa;
		}
		$fmt->setPattern("eeee, d MMMM yyyy");
		$html = "<tr class=\"intencje-dzień\"><td colspan=2 style=\"text-align: center\"><p>" . $fmt->format($date) . "</p>";
		if ($name != null) {
			$html .= "<p>$name</p>";
		}
		$html .= "</td></tr>";
		$fmt->setPattern("yyyy-MM-dd");
		$result = $wpdb->get_results( "SELECT * FROM `$intencje_table_name` WHERE DATE(`msza`) = DATE('{$date->format("Y-m-d")}');" );
		foreach ($result as $day) {
			$html .= intencje_row( date_create($day->msza)->format("H:i"), $day->intencja, $day->kaplica );
		}
		return $html;
	}
	
	function intencje_row( $hour, $intencje, $kaplica ) {
		$inner = "<p>$hour</p>";
		if ($kaplica != null) {
			$inner .= "<p><span class=\"kaplica\">$kaplica</span></p>";
		}
		return "<tr><td style=\"text-align: right\">$inner</td><td style=\"text-align: left\">$intencje</td></tr>";
	}
	
	/**
	 * Register a custom menu page.
	 */
	function intencje_register_my_custom_menu_page(){
		add_menu_page( 
			__( 'Intencje parafialne', 'textdomain' ),
			__( 'Intencje', 'textdomain' ),
			'manage_options',
			'intencje',
			'intencje_menu_page',
			'dashicons-book',
			6
		); 
	}
	add_action( 'admin_menu', 'intencje_register_my_custom_menu_page' );
	 
	/**
	 * Display a custom menu page
	 */
	function intencje_menu_page(){
		global $wpdb, $intencje_table_name, $intencje_nazwy_table_name;
		$intencje_table_name = $wpdb->prefix . 'intencje';
		$intencje_nazwy_table_name = $intencje_table_name . '_nazwy';
		
		$intencje_msze = $wpdb->get_results("SELECT `msza` FROM `$intencje_table_name`");
		$intencje_nazwy_daty = $wpdb->get_results("SELECT * FROM `$intencje_nazwy_table_name`");
		
		if ( isset ($_POST) ) {
			$datetime = $_POST["datetime"];
			$intencja_wprowadzona = $_POST["intencja_wprowadzona"];
			$intencja_usuwana = $_POST["intencja_usuwana"];
			$kaplica = $_POST["kaplica"];
			$date = $_POST["date"];
			$nazwa_usuwana = $_POST["nazwa_usuwana"];
			$nazwa = $_POST["dayname"];
			$_POST = array();
			
			if ($intencja_wprowadzona != null) {
				$wpdb->query("INSERT INTO `$intencje_table_name` (`msza`, `kaplica`, `intencja`) VALUES ('$datetime', '$kaplica', '$intencja_wprowadzona')");
			}
			if ($intencja_usuwana != null) {
				$wpdb->query("DELETE FROM `$intencje_table_name` WHERE DATE(`msza`) = DATE('$intencja_usuwana') AND TIME(`msza`) = TIME('$intencja_usuwana');");
			}
			
			if ($nazwa != null) {
				$wpdb->query("INSERT INTO `$intencje_nazwy_table_name` (`data`, `nazwa`) VALUES ('$date', '$nazwa')");
			}
			if ($nazwa_usuwana != null) {
				$wpdb->query("DELETE FROM `$intencje_nazwy_table_name` WHERE DATE(`data`) = DATE('$nazwa_usuwana');");
			}
		} ?>
		<script>
			function addNewIntention() {
				document.getElementById("intencje-main").style.display = 'none';
				document.getElementById("intencje-add").style.display = 'inherit';
			}
			function deleteIntention() {
				document.getElementById("intencje-main").style.display = 'none';
				document.getElementById("intencje-remove").style.display = 'inherit';
			}
			function cancelIntentionAdd() {
				document.getElementById("intencje-main").style.display = 'inherit';
				document.getElementById("intencje-add").style.display = 'none';
			}
			function cancelIntentionRemove() {
				document.getElementById("intencje-main").style.display = 'inherit';
				document.getElementById("intencje-remove").style.display = 'none';
			}
			function addNewDayName() {
				document.getElementById("intencje-nazwy-main").style.display = 'none';
				document.getElementById("intencje-nazwy-add").style.display = 'inherit';
			}
			function deleteDayName() {
				document.getElementById("intencje-nazwy-main").style.display = 'none';
				document.getElementById("intencje-nazwy-remove").style.display = 'inherit';
			}
			function cancelDayNameAdd() {
				document.getElementById("intencje-nazwy-main").style.display = 'inherit';
				document.getElementById("intencje-nazwy-add").style.display = 'none';
			}
			function cancelDayNameRemove() {
				document.getElementById("intencje-nazwy-main").style.display = 'inherit';
				document.getElementById("intencje-nazwy-remove").style.display = 'none';
			}
		</script>
		<style>
			.intencje {
				margin: 10px;
			}
		</style>
		<h1>Intencje parafialne</h1><hr/>
		<div class="intencje intencje-basic">
			<div id="intencje-main">
				<a class="button button-primary" onclick="addNewIntention()">Dodaj intencję</a>
				<a class="button remove-button" onclick="deleteIntention()">Usuń intencję</a>
			</div>
			<div id="intencje-add" style="display: none">
				<form style="margin: 10px;" method="post">
					<p><input name="datetime" type="datetime-local" min="<?= date("Y-m-d H:i") ?>" value="<?= date("Y-m-d H:i") ?>" pattern="YYYY-MM-DDThh:mm" required /></p>
					<p><label for="kaplica">Wpisz nazwę kaplicy, gdzie odprawiana będzie Msza w tej intencji (opcjonalnie):</label> <input name="kaplica" type="text" /></p>

					<?php 
					wp_editor(
						$intencja_w,
						'intencja_wprowadzona',
						array(
							'media_buttons' =>  false,
						)
					);
					?>
					<p><input type="submit" class="button-primary" value="Dodaj intencję" onclick="location.reload()" /> <input type="reset" class="button" />
					<input type="reset" class="button" value="Anuluj" onclick="cancelIntentionAdd()" /></p>
				</form>
			</div>
			<div id="intencje-remove" style="display: none">
				<form style="margin: 10px;" method="post">
					<p><select name="intencja_usuwana">
						<?= as_options($intencje_msze) ?>
					</select></p>
					<p><input type="submit" class="button-primary" value="Usuń intencję" onclick="location.reload()" /> <input type="reset" class="button" />
					<input type="reset" class="button" value="Anuluj" onclick="cancelIntentionRemove()" /></p>
				</form>
			</div>	
		</div>
		<div class="intencje intencje-nazwy">
			<div id="intencje-nazwy-main">
				<a class="button button-primary" onclick="addNewDayName()">Dodaj nazwę dnia</a>
				<a class="button remove-button" onclick="deleteDayName()">Usuń nazwę dnia</a>
			</div>
			<div id="intencje-nazwy-add" style="display: none">
				<form style="margin: 10px;" method="post">
					<p><input name="date" type="date" min="<?= date("Y-m-d") ?>" value="<?= date("Y-m-d") ?>" pattern="YYYY-MM-DD" required /></p>
					<p><label for="dayname">Wpisz nazwę dla dnia:</label> <input name="dayname" type="text" /></p>
					<p><input type="submit" class="button-primary" value="Dodaj nazwę dnia" onclick="location.reload()" /> <input type="reset" class="button" />
					<input type="reset" class="button" value="Anuluj" onclick="cancelDayNameAdd()" /></p>
				</form>
			</div>
			<div id="intencje-nazwy-remove" style="display: none">
				<form style="margin: 10px;" method="post">
					<p><select name="nazwa_usuwana">
						<?= as_options($intencje_nazwy_daty, true) ?>
					</select></p>
					<p><input type="submit" class="button-primary" value="Usuń nazwę dnia" onclick="location.reload()" /> <input type="reset" class="button" />
					<input type="reset" class="button" value="Anuluj" onclick="cancelDayNameRemove()" /></p>
				</form>
			</div>	
		</div>
	<?php
	}
	
	function as_options($array, $is_nazwy = false) {
		$html = '';
		foreach((array) $array as $elem) {
			if ($is_nazwy) {
				$html .= "<option>$elem->data</option>";
			} else $html .= "<option>$elem->msza</option>";
		}
		return $html;
	}
?>