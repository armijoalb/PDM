using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class player_controller : MonoBehaviour {

	private Rigidbody rb;
	private Animator anim;
	private Joystick joystick;

	// Use this for initialization
	void Start () {
		rb = GetComponent<Rigidbody> ();
		anim = GetComponent<Animator> ();
		joystick = FindObjectOfType<Joystick> ();
	}

	void FixedUpdate(){
		anim.SetFloat ("Velocity", Mathf.Abs (joystick.Horizontal + joystick.Vertical));
	}
	
	// Update is called once per frame
	void Update () {

		float x = joystick.Horizontal;
		float y = joystick.Vertical;
		Vector3 movement = new Vector3 (x, 0, y);
		rb.velocity = movement*1.5f;

		if(x != 0 && y != 0){
			Debug.Log(x.ToString() + " : " + y.ToString());
			transform.eulerAngles = new Vector3 (transform.eulerAngles.x,
				Mathf.Atan2 (x, y) * Mathf.Rad2Deg,
				transform.eulerAngles.z);
		}


	}
}
