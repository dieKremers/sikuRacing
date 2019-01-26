package application;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class CheckRankingDialogController extends AnchorPane {
	private Stage myStage = null;
	public void setMyStage(Stage myStage) {
		this.myStage = myStage;
	}

	private Hashtable<Car, Integer> ranking = new Hashtable<Car, Integer>();
	private ArrayList<Ranking> rankings = new ArrayList<Ranking>();
	
	@FXML
	private Button useRankingButton;
	
	@FXML
	private TableView<Ranking> rankingTable;
	
	public CheckRankingDialogController() {
		super();
	}

	@FXML
	public void useRankingButton()
	{
		generateReturnValue();
		myStage.close();
	}
	
	private void generateReturnValue() {
		ranking.clear();
		for(Ranking rank : rankings ) {
			if( rank.getRank() > 0 ) {
				ranking.put(rank.getCar(), rank.getRank());
			}
			else {
				ranking.put(rank.getCar(), 1001 );
			}
		}
	}

	public void setRanking( Hashtable<Car, Integer> originalRanking ) 
	{
		ranking.putAll(originalRanking);
		for(Map.Entry<Car, Integer> entry : ranking.entrySet()) {
			Ranking newRanking = new Ranking();
			newRanking.setCar(entry.getKey());
			if( entry.getValue() < 1000 ) {
				newRanking.setRank(entry.getValue());				
			}
			else {
				newRanking.setRank(0); //value for "not finished"
			}
			rankings.add(newRanking);
		}
		updateRankingTable();
	}
	
	public Hashtable<Car, Integer> getUpdatedRanking() {
		return ranking;
	}

	private void updateRankingTable()
	{
		if( rankingTable.getColumns().isEmpty() ) {
			initRankingTable();
		}
		ObservableList<Ranking> rankingList = FXCollections.observableArrayList();
		rankingList.addAll(rankings);
		rankingTable.setItems(rankingList);
		rankingTable.refresh();		
	}
	
	private void initRankingTable()
	{
		rankingTable.setEditable(true);
		
		TableColumn<Ranking, String> colDriverName = new TableColumn<Ranking, String>("Fahrer");
		TableColumn<Ranking, Integer> colRank = new TableColumn<Ranking, Integer>("Platzierung");
		
		colDriverName.setCellValueFactory(
				new PropertyValueFactory<Ranking, String>("stringDriver"));
		
		colRank.setCellValueFactory(
				new PropertyValueFactory<Ranking, Integer>("rank"));
		
		colDriverName.setMinWidth(100);
		colRank.setMinWidth(20);
		
		colRank.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colRank.setOnEditCommit(
				new EventHandler<CellEditEvent<Ranking,Integer>>(){
					@Override
					public void handle(CellEditEvent<Ranking,Integer> t) {
						t.getTableView().getItems().get(t.getTablePosition().getRow()).setRank(Integer.valueOf(t.getNewValue()));
					}
				});
		
		
		rankingTable.getColumns().addAll( colDriverName, colRank );
		
	}
}
