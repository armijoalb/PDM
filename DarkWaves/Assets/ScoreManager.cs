using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ScoreManager : MonoBehaviour {

	public int score = 0;
	private Text score_text;

	void Awake(){
		score_text = GetComponent<Text> ();
	}

	public void updateScore(int newScore){
		score = newScore;
		score_text.text = "Score: " + newScore;
	}
}
