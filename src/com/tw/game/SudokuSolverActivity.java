package com.tw.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.tw.game.checker.SolutionChecker;
import com.tw.game.result.Cell;
import com.tw.game.solver.SudokuSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SudokuSolverActivity extends Activity {
    private TextView selectedTextView;
    private List<List<Integer>> sudokuGrid = new ArrayList<>();
    private List<List<Integer>> puzzle = new ArrayList<>();
    private View popupView;
    private PopupWindow popupWindow;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.solver);
        SudokuActivity.addTextViews(this.sudokuGrid);
        setEditTextProperties();
    }

    public void showResult(View view) {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            puzzle.add(Arrays.asList(new Integer[9]));
            for (int j = 0; j < 9; j++) {
                EditText editText = (EditText) findViewById(sudokuGrid.get(i).get(j));
                if (editText.getText().toString().equals("")) {
                    count++;
                    puzzle.get(i).set(j, 0);
                } else puzzle.get(i).set(j, Integer.parseInt(editText.getText().toString()));
            }
        }
        String message = "Do you want solution for this puzzle ?";
        if (!new SolutionChecker().validateSolution(puzzle).isCorrect() || count <= 0) {
            Toast.makeText(this, "Invalid Puzzle", Toast.LENGTH_LONG).show();
            return;
        } else if (count == 81)
            message = "Do you want a solution for empty puzzle?";
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        showSolvePuzzle(new SudokuSolver(new SolutionChecker()).solvePuzzle(puzzle));
                    }
                }).setNegativeButton("No", null);
        builder.create().show();
    }

    public void clearPuzzle(View view) {
        setEditTextProperties();
    }

    public void loadPuzzle(View view) {
        finish();
        startActivity(new Intent(this, SudokuGeneratorActivity.class));
    }

    public void editField(View view) {
        SudokuActivity.editField(view, selectedTextView);
        popupWindow.dismiss();
        popupWindow = null;
    }

    public void clearNumber(View view) {
        SudokuActivity.clearNumber(selectedTextView);
        popupWindow.dismiss();
        popupWindow = null;
    }

    private void setEditTextProperties() {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                final EditText number = (EditText) findViewById(sudokuGrid.get(i).get(j));
                number.setText("");
                number.setInputType(InputType.TYPE_NULL);
                number.setKeyListener(null);
                number.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        selectedTextView = (TextView) view;
                        if (popupWindow == null)
                            popUpKeypad(number);

                        return false;
                    }
                });
            }
    }

    private void popUpKeypad(EditText number) {
        popupView = getLayoutInflater().inflate(R.layout.keypad, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ShapeDrawable());
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE)
                    popupWindow = null;
                return false;
            }
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(number);
    }

    private void showSolvePuzzle(List<List<Integer>> solvedPuzzle) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                EditText number = (EditText) findViewById(sudokuGrid.get(i).get(j));
                SudokuActivity.setTextColor(solvedPuzzle, i, j, number);
                SudokuActivity.setProperties(puzzle, solvedPuzzle, new Cell(i, j), number, 0, true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                confirmQuit();
                return false;
            }
        });
        return true;
    }

    private void confirmQuit() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SudokuSolverActivity.this);
        builder.setMessage("Do you want to quit this puzzle?").setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        finish();
                        startActivity(new Intent(SudokuSolverActivity.this, HomeActivity.class));
                    }
                }).setNegativeButton("No", null);
        builder.create().show();
    }
}